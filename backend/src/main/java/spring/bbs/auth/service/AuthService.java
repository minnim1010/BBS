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
import spring.bbs.auth.repository.TokenRepository;
import spring.bbs.common.exception.DataNotFoundException;
import spring.bbs.common.jwt.JwtProvider;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;


    @Transactional
    public LoginResponse login(LoginRequest req) {
        Authentication authentication = authenticateCredentials(req);
        log.debug("logined: {}", req.getName());

        String refreshToken = jwtProvider.generateRefreshToken(authentication);
        tokenRepository.saveRefreshToken(req.getName(), refreshToken, jwtProvider.getExpiration(refreshToken));

        String accessToken = jwtProvider.generateAccessToken(authentication);

        return new LoginResponse(accessToken, refreshToken);
    }

    private Authentication authenticateCredentials(LoginRequest req) {
        UsernamePasswordAuthenticationToken authenticationToken
            = new UsernamePasswordAuthenticationToken(req.getName(), req.getPassword());

        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    public AccessTokenResponse createNewAccessToken(CreateAccessTokenRequest req) {
        String refreshToken = req.getRefreshToken();
        validateRefreshToken(refreshToken);
        String memberName = jwtProvider.getName(refreshToken);
        checkExistedRefreshToken(memberName);

        Member member = findByName(memberName);
        String token = jwtProvider.generateAccessToken(member);

        return new AccessTokenResponse(token);
    }

    private Member findByName(String authorName) {
        return memberRepository.findByName(authorName).orElseThrow(
            () -> new DataNotFoundException("해당 refresh 토큰이 존재하지 않습니다."));
    }

    @Transactional
    public void logout(String token) {
        long expiration = jwtProvider.getExpiration(token);
        tokenRepository.saveAccessToken(token, expiration);

        String memberName = jwtProvider.getName(token);
        tokenRepository.deleteRefreshToken(memberName);
    }

    private void validateRefreshToken(String refreshToken) {
        if (!jwtProvider.isValidToken(refreshToken)) {
            throw new BadCredentialsException("Refresh 토큰이 유효하지 않습니다.");
        }
    }

    private void checkExistedRefreshToken(String name) {
        if (!tokenRepository.existsRefreshTokenByName(name)) {
            throw new BadCredentialsException("Refresh 토큰이 존재하지 않습니다.");
        }
    }
}
