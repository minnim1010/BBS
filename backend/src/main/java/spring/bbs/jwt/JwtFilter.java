package spring.bbs.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

    private final Logger logger = LoggerFactory.getLogger(
            this.getClass());

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpServletRequest);
        String requestUri = httpServletRequest.getRequestURI();
        authenticateToken(jwt, requestUri);

        chain.doFilter(request, response);
    }

    private boolean authenticateToken(String jwt, String requestUri) {
        if (StringUtils.hasText(jwt) && jwtProvider.isValidToken(jwt) && !jwtProvider.isLogoutAccessToken(jwt)) {
            Authentication authentication = jwtProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Get principal {}", authentication.getPrincipal());
            logger.debug("{} stored in context: {}", authentication.getName(), requestUri);
            return true;
        }

        logger.debug("No valid token: {}", requestUri);
        return false;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
            return bearerToken.substring(7);

        return null;
    }

}
