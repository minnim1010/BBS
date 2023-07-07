package spring.bbs.jwt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import spring.bbs.jwt.JwtProvider;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.LoginResponse;

@Service
public class JwtService {

    private final Logger logger = LoggerFactory.getLogger(
            JwtService.class);

    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public JwtService(JwtProvider jwtProvider,
                      AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.jwtProvider = jwtProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public LoginResponse login(LoginRequest req){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(req.getName(), req.getPassword());

        Authentication authentication = authenticationManagerBuilder
                .getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.createToken(authentication);

        logger.debug("logined {}", req.getName());

        return new LoginResponse(token);
    }
}
