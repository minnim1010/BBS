package spring.bbs.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtProvider implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long tokenValidMilSeconds;
    private final RedisTemplate<String, Object> redisTemplate;
    private Key key;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.token-validity-in-miliseconds}") long tokenValidMilSeconds,
                       RedisTemplate<String, Object> redisTemplate) {
        this.secret = secret;
        this.tokenValidMilSeconds = tokenValidMilSeconds;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long createdTime = (new Date()).getTime();
        Date expiredTime = new Date(createdTime + this.tokenValidMilSeconds);

        logger.debug("Authentication.getName: {}", authentication.getName());

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiredTime)
                .compact();
    }

    public Authentication getAuthentication(String token){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String role = claims.get(AUTHORITIES_KEY).toString();
        Authority authority = Enum.valueOf(Authority.class, role);
        List<GrantedAuthority> grantedAuthority = List.of(new SimpleGrantedAuthority(role));

        Member member = new Member(claims.getSubject(), "", "", true, authority);

        return new UsernamePasswordAuthenticationToken(
                member, token, grantedAuthority);
    }

    private Claims getClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getExpiration(String token){
        return getClaims(token).getExpiration().getTime();
    }

    public String getName(String token){
        return getClaims(token).getSubject();
    }

    public boolean isValidToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            logger.info("Unsupported JWT token.");
        } catch (MalformedJwtException | SignatureException e) {
            logger.info("Malformed JWT signature.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT token type is not matched.");
        }
        return false;
    }

    public boolean isLogoutToken(String token){
        return redisTemplate.opsForValue().get(token) != null;
    }
}
