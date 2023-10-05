package spring.bbs.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.bbs.common.util.CookieUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtResolver jwtResolver;
    private final JwtProperties jwtProperties;

    private static void handleUnsuccess(String requestUri) {
        log.debug("No valid token: {}", requestUri);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveAccessToken(request);
        String refreshToken = resolveRefreshToken(request);
        String requestUri = request.getRequestURI();

        if (verify(accessToken)) {
            handleSuccess(accessToken, requestUri);
        } else if (verify(refreshToken)) {
            createNewAccessTokenAndHandleSuccess(response, refreshToken, requestUri);
        } else {
            handleUnsuccess(requestUri);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        return CookieUtil.getCookieValue(request, jwtProperties.getAccessTokenCookieName())
            .orElse(null);
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        return CookieUtil.getCookieValue(request, jwtProperties.getRefreshTokenCookieName())
            .orElse(null);
    }

    private boolean verify(String token) {
        return StringUtils.hasText(token)
            && jwtProvider.isValidToken(token)
            && !jwtProvider.isLogoutAccessToken(token);
    }

    private void handleSuccess(String token, String requestUri) {
        Authentication authentication = jwtResolver.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("{}: {} stored in context: {}",
            jwtResolver.getAuthorities(token), authentication.getName(), requestUri);
    }

    private void createNewAccessTokenAndHandleSuccess(
        HttpServletResponse response, String refreshToken, String requestUri) {
        Authentication authentication = jwtResolver.getAuthentication(refreshToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Date expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
        String accessToken = jwtProvider.createToken(authentication, expiredTime);

        CookieUtil.addCookie(response, jwtProperties.getAccessTokenCookieName(), accessToken,
            jwtProperties.getAccessTokenDuration().getSeconds());

        log.debug("{}: {} stored in context: {}",
            jwtResolver.getAuthorities(refreshToken), authentication.getName(), requestUri);
    }
}
