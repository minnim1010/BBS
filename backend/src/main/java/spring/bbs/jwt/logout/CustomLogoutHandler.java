package spring.bbs.jwt.logout;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;
import spring.bbs.exceptionhandler.exception.AuthorizeException;
import spring.bbs.jwt.JwtProvider;

import java.util.concurrent.TimeUnit;

public class CustomLogoutHandler implements LogoutHandler {

    private final Logger logger = LoggerFactory.getLogger(
            CustomLogoutHandler.class);

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    public CustomLogoutHandler(JwtProvider jwtProvider, RedisTemplate<String, Object> redisTemplate) {
        this.jwtProvider = jwtProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String headerToken = request.getHeader("Authorization");
        logger.debug("headerToken: {}", headerToken);
        if (!StringUtils.hasText(headerToken) || !headerToken.startsWith("Bearer "))
            throw new AuthorizeException("Token not found.");

        String token = headerToken.substring(7);
        if (!jwtProvider.isValidToken(token))
            throw new AuthorizeException("No valid token.");

        long expiration = jwtProvider.getExpiration(token);
        logger.debug("expiration: {}", expiration);
        redisTemplate.opsForValue().set(token, "access_token", expiration, TimeUnit.SECONDS);

        authentication = null;
    }
}
