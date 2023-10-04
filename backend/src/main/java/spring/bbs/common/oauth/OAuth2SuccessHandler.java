package spring.bbs.common.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import spring.bbs.auth.domain.RefreshToken;
import spring.bbs.auth.repository.TokenRepository;
import spring.bbs.common.jwt.JwtProperties;
import spring.bbs.common.jwt.JwtProvider;
import spring.bbs.common.jwt.JwtResolver;
import spring.bbs.common.util.CookieUtil;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepositoryHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String REDIRECT_PATH = "/home";

    private final JwtProvider jwtProvider;
    private final JwtResolver jwtResolver;
    private final JwtProperties jwtProperties;

    private final TokenRepository tokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final MemberRepositoryHandler memberUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Member member = memberUtil.findByEmail((String) oAuth2User.getAttributes().get("email"));

        String refreshToken = jwtProvider.createToken(
            member, jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now()));
        tokenRepository.save(new RefreshToken(member.getName(), refreshToken), jwtResolver.getExpirationTime(refreshToken));
        addRefreshTokenToCookie(request, response, refreshToken);
        
        String accessToken = jwtProvider.createToken(
            member, jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now()));
        String targetUrl = getTargetUrl(accessToken);

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) jwtProperties.getRefreshTokenDuration().toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
            .queryParam("token", token)
            .build()
            .toUriString();
    }
}
