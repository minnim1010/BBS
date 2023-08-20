package spring.helper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import spring.bbs.common.jwt.JwtProvider;
import spring.bbs.member.domain.Authority;

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

    public String getJwtToken() {
        Authentication token =
            new UsernamePasswordAuthenticationToken(
                memberName, "", List.of(new SimpleGrantedAuthority(Authority.ROLE_USER.name())));
        return jwtProvider.generateAccessToken(token);
    }

    public String getJwtToken(String name) {
        Authentication token =
            new UsernamePasswordAuthenticationToken(
                name, "", List.of(new SimpleGrantedAuthority(Authority.ROLE_USER.name())));
        return jwtProvider.generateAccessToken(token);
    }

    public String getAccessTokenWithAdminRole() {
        Authentication token =
            new UsernamePasswordAuthenticationToken(
                memberName, "", List.of(new SimpleGrantedAuthority(Authority.ROLE_ADMIN.name())));
        return jwtProvider.generateAccessToken(token);
    }

    public String getUserRoleTokenWithHeaderPrefix() {
        String token = getJwtToken();
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
