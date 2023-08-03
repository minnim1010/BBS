package spring.bbs.jwt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import spring.bbs.jwt.JwtProvider;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.LoginResponse;

import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    private final Logger logger = LoggerFactory.getLogger(
            this.getClass());

    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtService(JwtProvider jwtProvider,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            RedisTemplate<String, Object> redisTemplate) {
        this.jwtProvider = jwtProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.redisTemplate = redisTemplate;
    }

    public LoginResponse login(LoginRequest req) {
        logger.debug(req.toString());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(req.getName(),
                req.getPassword());

        logger.debug("usernamepasswordfilter = {}", SecurityContextHolder.getContext().getAuthentication());

        Authentication authentication = authenticationManagerBuilder
                .getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.createToken(authentication);

        logger.debug("logined: {}", req.getName());

        return new LoginResponse(token);
    }

    public void logout(String token) {
        long expiration = jwtProvider.getExpiration(token);
        redisTemplate.opsForValue().set(token, "access_token", expiration, TimeUnit.SECONDS);
    }
}
