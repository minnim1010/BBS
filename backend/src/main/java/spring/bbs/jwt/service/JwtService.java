package spring.bbs.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import spring.bbs.jwt.JwtProvider;
import spring.bbs.jwt.dto.request.CreateAccessTokenRequest;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.AccessTokenResponse;
import spring.bbs.jwt.dto.response.LoginResponse;
import spring.bbs.jwt.repository.TokenRepository;
import spring.bbs.member.domain.Member;
import spring.bbs.member.service.MemberService;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberService memberService;
    private final TokenRepository tokenRepository;

    private Authentication authenticateCredentials(LoginRequest req){
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(req.getName(), req.getPassword());

        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    public LoginResponse login(LoginRequest req) {
        Authentication authentication = authenticateCredentials(req);
        log.debug("logined: {}", req.getName());

        String refreshToken = jwtProvider.generateRefreshToken(authentication);
        tokenRepository.saveRefreshToken(refreshToken, jwtProvider.getExpiration(refreshToken));

        String accessToken = jwtProvider.generateAccessToken(authentication);

        return new LoginResponse(accessToken, refreshToken);
    }

    public AccessTokenResponse createNewAccessToken(CreateAccessTokenRequest req){
        String refreshToken = req.getRefreshToken();
        validateRefreshToken(refreshToken);

        Member member = memberService.findByName(jwtProvider.getName(refreshToken));
        String token = jwtProvider.generateAccessToken(member);

        return new AccessTokenResponse(token);
    }

    public void logout(String token) {
        if(!jwtProvider.isValidToken(token))
            throw new BadCredentialsException("Access 토큰이 유효하지 않습니다.");
        long expiration = jwtProvider.getExpiration(token);
        tokenRepository.saveAccessToken(token, expiration);
    }

    private void validateRefreshToken(String refreshToken){
        if(!jwtProvider.isValidToken(refreshToken))
            throw new BadCredentialsException("Refresh 토큰이 유효하지 않습니다.");

        if(!tokenRepository.existsByRefreshToken(refreshToken)){
            throw new BadCredentialsException("Refresh 토큰이 존재하지 않습니다.");
        }
    }
}
