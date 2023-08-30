package spring.bbs.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.bbs.auth.controller.dto.request.CreateAccessTokenRequest;
import spring.bbs.auth.controller.dto.request.LoginRequest;
import spring.bbs.auth.controller.dto.response.AccessTokenResponse;
import spring.bbs.auth.controller.dto.response.LoginResponse;
import spring.bbs.auth.domain.AccessToken;
import spring.bbs.auth.domain.RefreshToken;
import spring.bbs.auth.repository.TokenRepository;
import spring.bbs.common.exceptionhandling.exception.DataNotFoundException;
import spring.bbs.common.jwt.JwtProvider;
import spring.bbs.common.jwt.JwtResolver;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

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
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    @Transactional
    public LoginResponse login(LoginRequest req) {
        Authentication authentication = authenticateCredentials(req);
        log.debug("logined: {}", req.getName());

        Date expiredTime = jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now());
        String refreshToken = jwtProvider.createToken(authentication, expiredTime);
        long timeout = expiredTime.getTime() - System.currentTimeMillis();
        tokenRepository.save(
            new RefreshToken(req.getName(), refreshToken), timeout);

        expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
        String accessToken = jwtProvider.createToken(authentication, expiredTime);

        return new LoginResponse(accessToken, refreshToken);
    }

    private Authentication authenticateCredentials(LoginRequest req) {
        UsernamePasswordAuthenticationToken authenticationToken
            = new UsernamePasswordAuthenticationToken(req.getName(), req.getPassword());

        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    public AccessTokenResponse createNewAccessToken(CreateAccessTokenRequest req) {
        String refreshToken = req.getRefreshToken();
        verify(refreshToken);
        String memberName = jwtResolver.getName(refreshToken);
        checkRefreshTokenExists(memberName);

        Member member = findByName(memberName);
        Date expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
        String token = jwtProvider.createToken(member, expiredTime);

        return new AccessTokenResponse(token);
    }

    private Member findByName(String authorName) {
        return memberRepository.findByName(authorName).orElseThrow(
            () -> new DataNotFoundException("해당 refresh 토큰이 존재하지 않습니다."));
    }

    @Transactional
    public void logout(String token) {
        long expiration = jwtResolver.getExpirationTime(token);
        String memberName = jwtResolver.getName(token);
        long timeout = expiration - System.currentTimeMillis();
        tokenRepository.save(new AccessToken(memberName, token), timeout);

        tokenRepository.delete(new RefreshToken(memberName));
    }

    private void verify(String refreshToken) {
        if (!jwtProvider.isValidToken(refreshToken)) {
            throw new BadCredentialsException("Refresh 토큰이 유효하지 않습니다.");
        }
    }

    private void checkRefreshTokenExists(String name) {
        if (!tokenRepository.exists(new RefreshToken(name))) {
            throw new BadCredentialsException("Refresh 토큰이 존재하지 않습니다.");
        }
    }
}
