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

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;
    private final JwtResolver jwtResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        String requestUri = request.getRequestURI();

        if (validate(token)) {
            Authentication authentication = jwtResolver.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("{}: {} stored in context: {}", jwtResolver.getAuthorities(token), authentication.getName(), requestUri);
        } else {
            log.debug("No valid token: {}", requestUri);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String tokenHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(tokenHeader) && tokenHeader.startsWith(TOKEN_PREFIX)) {
            return tokenHeader.substring(7);
        }
        return null;
    }

    private boolean validate(String token) {
        return StringUtils.hasText(token) &&
            jwtProvider.isValidToken(token) &&
            !jwtProvider.isLogoutAccessToken(token);
    }
}
