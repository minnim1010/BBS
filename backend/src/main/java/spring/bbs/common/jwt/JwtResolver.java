package spring.bbs.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;

import java.security.Key;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtResolver implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";

    private final JwtProperties jwtProperties;
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        key = Keys.hmacShaKeyFor(keyBytes);
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
