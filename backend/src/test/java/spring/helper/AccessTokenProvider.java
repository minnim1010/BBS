package spring.helper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import spring.bbs.common.jwt.JwtProvider;
import spring.bbs.member.domain.Authority;

import java.time.LocalDateTime;
import java.util.List;

public class AccessTokenProvider {
    public final String AUTHENTICATION_HEADER = "Authorization";
    public final String TOKEN_PREFIX = "Bearer ";

    private final String memberName;
    private final JwtProvider jwtProvider;

    public AccessTokenProvider(JwtProvider jwtProvider, String memberName) {
        this.memberName = memberName;
        this.jwtProvider = jwtProvider;
    }

    public String createAccessToken() {
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                memberName, "", List.of(new SimpleGrantedAuthority(Authority.ROLE_USER.name())));
        return jwtProvider.createToken(authentication, jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now()));
    }

    public String createAccessToken(String name) {
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                name, "", List.of(new SimpleGrantedAuthority(Authority.ROLE_USER.name())));
        return jwtProvider.createToken(authentication, jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now()));
    }

    public String getAccessTokenWithAdminRole() {
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                memberName, "", List.of(new SimpleGrantedAuthority(Authority.ROLE_ADMIN.name())));
        return jwtProvider.createToken(authentication, jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now()));
    }

    public String getUserRoleTokenWithHeaderPrefix() {
        String token = createAccessToken();
        return TOKEN_PREFIX + token;
    }

    public String getUserRoleTokenWithHeaderPrefix(String token) {
        return TOKEN_PREFIX + token;
    }

    public String getAdminRoleTokenWithHeaderPrefix() {
        String token = getAccessTokenWithAdminRole();
        return TOKEN_PREFIX + token;
    }
}
