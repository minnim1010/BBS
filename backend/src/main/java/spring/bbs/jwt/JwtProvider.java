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
import spring.bbs.member.domain.Member;

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
    private final TokenRepository tokenRepository;
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

    public String generateAccessToken(Member member) {
        Date expiredTime = new Date(new Date().getTime() + jwtProperties.getAccessTokenDuration().toMillis());
        return createToken(member, expiredTime);
    }

    public String generateRefreshToken(Authentication authentication) {
        Date expiredTime = new Date(new Date().getTime() + jwtProperties.getRefreshTokenDuration().toMillis());
        return createToken(authentication, expiredTime);
    }

    public String generateRefreshToken(Member member) {
        Date expiredTime = new Date(new Date().getTime() + jwtProperties.getRefreshTokenDuration().toMillis());
        return createToken(member, expiredTime);
    }

    private String createToken(Authentication authentication, Date expiredTime){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiredTime)
                .compact();
    }

    private String createToken(Member member, Date expiredTime){
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(member.getName())
                .claim(AUTHORITIES_KEY, member.getAuthority())
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
            log.info("토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 토큰 형식입니다. ");
        } catch (MalformedJwtException | SignatureException e) {
            log.info("토큰이 위조되었습니다.");
        } catch (IllegalArgumentException e) {
            log.info("토큰 타입이 올바르지 않습니다.");
        }
        return false;
    }

    public boolean isLogoutAccessToken(String token){
        return tokenRepository.existsByAccessToken(token);
    }
}
