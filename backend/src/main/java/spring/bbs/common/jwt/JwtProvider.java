package spring.bbs.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import spring.bbs.auth.domain.AccessToken;
import spring.bbs.auth.repository.TokenRepository;
import spring.bbs.member.domain.Member;
import spring.bbs.util.TimeUtil;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "role";

    private final JwtProperties jwtProperties;
    private final JwtResolver jwtResolver;

    private final TokenRepository tokenRepository;
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Date calAccessTokenExpirationTime(LocalDateTime now) {
        LocalDateTime expired = now.plusSeconds(jwtProperties.getAccessTokenDuration().toSeconds());
        return TimeUtil.convertLocalDateTimeToDate(expired, ZoneId.systemDefault());
    }

    public Date calRefreshTokenExpirationTime(LocalDateTime now) {
        LocalDateTime expired = now.plusSeconds(jwtProperties.getRefreshTokenDuration().toSeconds());
        return TimeUtil.convertLocalDateTimeToDate(expired, ZoneId.systemDefault());
    }

    public String createToken(Authentication authentication, Date expirationTime) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(expirationTime)
            .compact();
    }

    public String createToken(Member member, Date expirationTime) {
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setSubject(member.getName())
            .claim(AUTHORITIES_KEY, member.getAuthority())
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(expirationTime)
            .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException |
                 IllegalArgumentException |
                 UnsupportedJwtException |
                 MalformedJwtException |
                 SignatureException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean isLogoutAccessToken(String token) {
        return tokenRepository.exists(
            new AccessToken(jwtResolver.getName(token), token));
    }
}
