package spring.bbs.common.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import spring.bbs.auth.domain.RefreshToken;
import spring.bbs.auth.repository.TokenRepository;
import spring.bbs.common.constant.Api;
import spring.bbs.common.jwt.JwtProperties;
import spring.bbs.common.jwt.JwtProvider;
import spring.bbs.common.util.CookieUtil;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepositoryHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REDIRECT_PATH = "/";

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    private final TokenRepository tokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final MemberRepositoryHandler memberUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Member member = memberUtil.findByEmail((String) oAuth2User.getAttributes().get("email"));

        String accessToken = createAccessToken(member);
        String refreshToken = createRefreshToken(member);
        CookieUtil.addCookie(response, jwtProperties.getAccessTokenCookieName(), accessToken,
            jwtProperties.getAccessTokenDuration().getSeconds());
        CookieUtil.addCookie(response, jwtProperties.getRefreshTokenCookieName(), refreshToken,
            jwtProperties.getRefreshTokenDuration().getSeconds());

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, Api.FRONT_ORIGIN + REDIRECT_PATH);
    }

    private String createAccessToken(Member member) {
        Date expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
        return jwtProvider.createToken(member, expiredTime);
    }

    private String createRefreshToken(Member member) {
        Date expiredTime = jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now());
        String refreshToken = jwtProvider.createToken(member, expiredTime);

        long timeout = expiredTime.getTime() - System.currentTimeMillis();
        tokenRepository.save(
            new RefreshToken(member.getName(), refreshToken), timeout);

        return refreshToken;
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
