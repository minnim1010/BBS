package spring.bbs.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import spring.bbs.jwt.repository.TokenRepository;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";

    private final JwtProperties jwtProperties;
    private final TokenRepository TokenRepository;
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Authentication authentication) {
        Date expiredTime = new Date(new Date().getTime() + jwtProperties.getAccessTokenDuration().toMillis());
        return createToken(authentication, expiredTime);
    }

    public String generateRefreshToken(Authentication authentication) {
        Date expiredTime = new Date(new Date().getTime() + jwtProperties.getRefreshTokenDuration().toMillis());
        return createToken(authentication, expiredTime);
    }

    private String createToken(Authentication authentication, Date expiredTime){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        log.debug(authorities);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiredTime)
                .compact();
    }

    public Authentication getAuthentication(String token){
        Claims claims = getClaims(token);
        String role = claims.get(AUTHORITIES_KEY).toString();
        log.debug(role);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return new UsernamePasswordAuthenticationToken(
                new User(claims.getSubject(), "", authorities), token, authorities);
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
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (MalformedJwtException | SignatureException e) {
            log.info("Malformed JWT signature.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token type is not matched.");
        }
        return false;
    }

    public boolean isLogoutAccessToken(String token){
        return TokenRepository.existsAccessToken(token);
    }
}
