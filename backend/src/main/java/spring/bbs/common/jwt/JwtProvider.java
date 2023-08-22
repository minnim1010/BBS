package spring.bbs.common.jwt;

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
import org.springframework.stereotype.Component;
import spring.bbs.auth.domain.AccessToken;
import spring.bbs.auth.repository.TokenRepository;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Date calAccessTokenExpirationTime(LocalDateTime now) {
        ZonedDateTime zdt = ZonedDateTime.of(now, ZoneId.systemDefault());
        long date = zdt.toInstant().toEpochMilli();

        return new Date(date + jwtProperties.getAccessTokenDuration().toMillis());
    }

    public Date calRefreshTokenExpirationTime(LocalDateTime now) {
        ZonedDateTime zdt = ZonedDateTime.of(now, ZoneId.systemDefault());
        long date = zdt.toInstant().toEpochMilli();

        return new Date(date + jwtProperties.getRefreshTokenDuration().toMillis());
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

    public boolean isLogoutAccessToken(String token) {
        return tokenRepository.exists(new AccessToken(getName(token)));
    }

    private Claims getClaims(String token) {
        return Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    public String getName(String token) {
        return getClaims(token).getSubject();
    }

    public String getAuthorities(String token) {
        return getClaims(token).get(AUTHORITIES_KEY).toString();
    }

    public long getExpirationTime(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String role = claims.get(AUTHORITIES_KEY).toString();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        Member currentMember = Member.builder()
            .name(claims.getSubject())
            .authority(Authority.ROLE_USER)
            .build();

        return new UsernamePasswordAuthenticationToken(
            currentMember, token, authorities);
    }
}
