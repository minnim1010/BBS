package spring.bbs.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import spring.bbs.jwt.JwtProvider;
import spring.bbs.jwt.dto.request.CreateAccessTokenRequest;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.AccessTokenResponse;
import spring.bbs.jwt.dto.response.LoginResponse;
import spring.bbs.jwt.repository.TokenRepository;
import spring.bbs.member.domain.Member;
import spring.bbs.util.MemberUtil;
import spring.bbs.util.RoleType;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenRepository TokenRepository;
    private final MemberUtil memberUtil;

    public LoginResponse login(LoginRequest req) {
        Authentication authentication = authenticateCredentials(req);
        log.debug("logined: {}", req.getName());

        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        long expiration = jwtProvider.getExpiration(refreshToken);
        TokenRepository.saveRefreshToken(refreshToken, expiration);

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
        if(!jwtProvider.isValidToken(refreshToken) || !TokenRepository.existsRefreshToken(refreshToken))
            throw new BadCredentialsException("No valid refresh token.");

        String memberName = jwtProvider.getName(refreshToken);
        Member member = memberUtil.getMember(memberName);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(RoleType.user));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        new User(member.getName(), "", authorities), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateAccessToken(authentication);

        return new AccessTokenResponse(token);
    }

    public void logout(String token) {
        long expiration = jwtProvider.getExpiration(token);
        TokenRepository.saveAccessToken(token, expiration);
    }
}
