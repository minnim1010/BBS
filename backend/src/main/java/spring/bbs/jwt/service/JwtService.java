package spring.bbs.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.jwt.JwtProvider;
import spring.bbs.jwt.dto.request.CreateAccessTokenRequest;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.AccessTokenResponse;
import spring.bbs.jwt.dto.response.LoginResponse;
import spring.bbs.jwt.repository.RefreshToken;
import spring.bbs.jwt.repository.RefreshTokenRepository;
import spring.bbs.jwt.repository.TokenRepository;
import spring.bbs.member.domain.Member;
import spring.bbs.member.service.MemberService;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberService memberService;
    private final TokenRepository tokenRepository;

    public LoginResponse login(LoginRequest req) {

        Authentication authentication = authenticateCredentials(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("logined: {}", req.getName());

        String accessToken = jwtProvider.generateAccessToken(authentication);

        String refreshToken = jwtProvider.generateRefreshToken(authentication);
        Member member = memberService.findByName(req.getName());
        refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken));

        return new LoginResponse(accessToken, refreshToken);
    }
    
    private Authentication authenticateCredentials(LoginRequest req){
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(req.getName(), req.getPassword());

        log.debug("usernamepasswordfilter = {}", SecurityContextHolder.getContext().getAuthentication());

        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    public AccessTokenResponse createNewAccessToken(CreateAccessTokenRequest req){
        String refreshToken = req.getRefreshToken();
        if(!jwtProvider.isValidToken(refreshToken))
            throw new BadCredentialsException("No valid refresh token.");

        Long memberId = this.findByRefreshToken(req.getRefreshToken()).getMemberId();

        Member member = memberService.findById(memberId);
        String token = jwtProvider.generateAccessToken(member);

        return new AccessTokenResponse(token);
    }

    public void logout(String token) {
        if(!jwtProvider.isValidToken(token))
            throw new BadCredentialsException("Access token is not valid");
        long expiration = jwtProvider.getExpiration(token);
        tokenRepository.saveAccessToken(token, expiration);
    }

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new DataNotFoundException("Refresh token doesn't exist."));
    }
}
