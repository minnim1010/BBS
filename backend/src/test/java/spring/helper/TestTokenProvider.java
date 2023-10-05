package spring.helper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import spring.bbs.auth.controller.dto.response.LoginResponse;
import spring.bbs.common.jwt.JwtProvider;
import spring.bbs.member.domain.Authority;

import java.time.LocalDateTime;
import java.util.List;

public class TestTokenProvider {
    private final JwtProvider jwtProvider;

    public TestTokenProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public LoginResponse getTokenWithAdminRole(String name) {
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                name, "", List.of(new SimpleGrantedAuthority(Authority.ROLE_ADMIN.name())));
        String accessToken = jwtProvider.createToken(authentication,
            jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now()));
        String refreshToken = jwtProvider.createToken(authentication,
            jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now()));

        return new LoginResponse(refreshToken, accessToken);
    }

    public LoginResponse getTokenWithUserRole(String name) {
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                name, "", List.of(new SimpleGrantedAuthority(Authority.ROLE_USER.name())));
        String accessToken = jwtProvider.createToken(authentication,
            jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now()));
        String refreshToken = jwtProvider.createToken(authentication,
            jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now()));

        return new LoginResponse(refreshToken, accessToken);
    }
}
