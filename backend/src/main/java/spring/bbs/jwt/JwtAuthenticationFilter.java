package spring.bbs.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);
        String requestUri = request.getRequestURI();

        if (StringUtils.hasText(jwt) &&
                jwtProvider.isValidToken(jwt) &&
                !jwtProvider.isLogoutAccessToken(jwt)) {
            Authentication authentication = jwtProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("{} stored in context: {}", authentication.getName(), requestUri);
        }else
            log.debug("No valid token: {}", requestUri);

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String tokenHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(tokenHeader) && tokenHeader.startsWith(TOKEN_PREFIX))
            return tokenHeader.substring(7);

        return null;
    }
}
