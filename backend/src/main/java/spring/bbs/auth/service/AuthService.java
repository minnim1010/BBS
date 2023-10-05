package spring.bbs.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.bbs.auth.controller.dto.request.LoginRequest;
import spring.bbs.auth.controller.dto.response.LoginResponse;
import spring.bbs.auth.domain.AccessToken;
import spring.bbs.auth.domain.RefreshToken;
import spring.bbs.auth.repository.TokenRepository;
import spring.bbs.common.jwt.JwtProvider;
import spring.bbs.common.jwt.JwtResolver;

import java.time.LocalDateTime;
import java.util.Date;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final JwtResolver jwtResolver;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenRepository tokenRepository;

    @Transactional
    public LoginResponse login(LoginRequest req) {
        Authentication authentication = authenticateCredentials(req);
        log.debug("logined: {}", req.getName());

        String accessToken = createAccessToken(authentication);
        String refreshToken = createRefreshToken(authentication);
        return new LoginResponse(refreshToken, accessToken);
    }

    private Authentication authenticateCredentials(LoginRequest req) {
        UsernamePasswordAuthenticationToken authenticationToken
            = new UsernamePasswordAuthenticationToken(req.getName(), req.getPassword());
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    private String createAccessToken(Authentication authentication) {
        Date expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
        return jwtProvider.createToken(authentication, expiredTime);
    }

    private String createRefreshToken(Authentication authentication) {
        Date expiredTime = jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now());
        String refreshToken = jwtProvider.createToken(authentication, expiredTime);

        long timeout = expiredTime.getTime() - System.currentTimeMillis();
        tokenRepository.save(
            new RefreshToken(authentication.getName(), refreshToken), timeout);

        return refreshToken;
    }

    @Transactional
    public void logout(String accessToken) {
        long expiration = jwtResolver.getExpirationTime(accessToken);
        long timeout = expiration - System.currentTimeMillis();
        String memberName = jwtResolver.getName(accessToken);
        tokenRepository.save(new AccessToken(memberName, accessToken), timeout);

        tokenRepository.delete(new RefreshToken(memberName));
    }
}
