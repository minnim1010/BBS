package spring.bbs.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.IntegrationTestConfig;
import spring.bbs.auth.controller.dto.request.CreateAccessTokenRequest;
import spring.bbs.auth.controller.dto.request.LoginRequest;
import spring.bbs.auth.controller.dto.response.AccessTokenResponse;
import spring.bbs.auth.controller.dto.response.LoginResponse;
import spring.bbs.auth.domain.AccessToken;
import spring.bbs.auth.domain.RefreshToken;
import spring.bbs.auth.repository.TokenRepository;
import spring.bbs.auth.service.AuthService;
import spring.bbs.common.jwt.JwtProvider;
import spring.bbs.common.jwt.JwtResolver;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class AuthServiceTest extends IntegrationTestConfig {
    private static final String MEMBER_NAME = "AuthTestUser";

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private JwtResolver jwtResolver;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate StringRedisTemplate;

    @AfterEach
    void setUp() {
        memberRepository.deleteAllInBatch();
        StringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Nested
    @DisplayName("로그인 요청 시 ")
    class Login {
        @DisplayName("아이디, 비밀번호가 유효하면 refresh, access 토큰을 반환한다.")
        @Test
        void successReturn() {
            //given
            Member member = Member.builder()
                .name(MEMBER_NAME)
                .password(passwordEncoder.encode(MEMBER_NAME))
                .email(MEMBER_NAME + "@test.com")
                .isEnabled(true)
                .authority(Authority.ROLE_USER)
                .build();
            memberRepository.save(member);

            LoginRequest req = new LoginRequest(MEMBER_NAME, MEMBER_NAME);

            //when
            LoginResponse response = authService.login(req);

            //then
            String accessToken = response.getAccessToken();
            String refreshToken = response.getRefreshToken();

            assertThat(jwtProvider.isValidToken(accessToken)).isTrue();
            assertThat(jwtProvider.isValidToken(refreshToken)).isTrue();

            assertThat(jwtResolver.getName(accessToken)).isEqualTo(req.getName());
            assertThat(jwtResolver.getName(refreshToken)).isEqualTo(req.getName());

            assertThat(tokenRepository.exists(new RefreshToken(req.getName()))).isTrue();
        }

        @DisplayName("없는 아이디라면 토큰을 발급하지 않는다.")
        @Test
        void failWithNonExistedName() {
            //given
            Member member = Member.builder()
                .name(MEMBER_NAME)
                .password(passwordEncoder.encode(MEMBER_NAME))
                .email(MEMBER_NAME + "@test.com")
                .isEnabled(true)
                .authority(Authority.ROLE_USER)
                .build();
            memberRepository.save(member);

            LoginRequest req = new LoginRequest("NonExistedName", MEMBER_NAME);

            //when then
            assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class);
        }

        @DisplayName("비밀번호가 틀리다면 토큰을 발급하지 않는다.")
        @Test
        void failWithInvalidPassword() {
            //given
            Member member = Member.builder()
                .name(MEMBER_NAME)
                .password(passwordEncoder.encode(MEMBER_NAME))
                .email(MEMBER_NAME + "@test.com")
                .isEnabled(true)
                .authority(Authority.ROLE_USER)
                .build();
            memberRepository.save(member);

            LoginRequest req = new LoginRequest(MEMBER_NAME, "invalidPassword");

            //when then
            assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class);
        }
    }

    @Nested
    @DisplayName("새 토큰 발급 요청 시 ")
    class CreateNewAccessToken {

        @DisplayName("refresh token이 유효하면 새 access token을 발급한다.")
        @Test
        void successReturnNewAccessToken() {
            //given
            Member member = Member.builder()
                .name(MEMBER_NAME)
                .password(passwordEncoder.encode(MEMBER_NAME))
                .email(MEMBER_NAME + "@test.com")
                .isEnabled(true)
                .authority(Authority.ROLE_USER)
                .build();
            memberRepository.save(member);

            Date expiredTime = jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now());
            String refreshToken = jwtProvider.createToken(member, expiredTime);

            long timeout = jwtResolver.getExpirationTime(refreshToken) - System.currentTimeMillis();
            tokenRepository.save(new RefreshToken(member.getName(), refreshToken), timeout);

            CreateAccessTokenRequest req = new CreateAccessTokenRequest(refreshToken);

            //when
            AccessTokenResponse response = authService.createNewAccessToken(req);

            //then
            String accessToken = response.getToken();

            assertThat(tokenRepository.exists(new RefreshToken(member.getName()))).isTrue();
            assertThat(jwtProvider.isValidToken(accessToken)).isTrue();
            assertThat(jwtResolver.getName(accessToken)).isEqualTo(member.getName());
        }

        @DisplayName("refresh token이 유효하지 않다면 새 토큰을 발급하지 않는다.")
        @Test
        void failWithInvalidRefreshToken() {
            //given
            Member member = Member.builder()
                .name(MEMBER_NAME)
                .password(passwordEncoder.encode(MEMBER_NAME))
                .email(MEMBER_NAME + "@test.com")
                .isEnabled(true)
                .authority(Authority.ROLE_USER)
                .build();
            memberRepository.save(member);

            Date expiredTime = jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now());
            String refreshToken = jwtProvider.createToken(member, expiredTime);

            long timeout = jwtResolver.getExpirationTime(refreshToken) - System.currentTimeMillis();
            tokenRepository.save(new RefreshToken(member.getName(), refreshToken), timeout);

            CreateAccessTokenRequest req = new CreateAccessTokenRequest("invalidRefreshToken");

            //when then
            assertThatThrownBy(() -> authService.createNewAccessToken(req))
                .isInstanceOf(BadCredentialsException.class);
        }

        @DisplayName("refresh token이 존재하지 않으면 새 토큰을 발급하지 않는다.")
        @Test
        void failWithNonExistedRefreshToken() {
            //given
            Member member = Member.builder()
                .name(MEMBER_NAME)
                .password(passwordEncoder.encode(MEMBER_NAME))
                .email(MEMBER_NAME + "@test.com")
                .isEnabled(true)
                .authority(Authority.ROLE_USER)
                .build();
            memberRepository.save(member);

            Date expiredTime = jwtProvider.calRefreshTokenExpirationTime(LocalDateTime.now());
            String refreshToken = jwtProvider.createToken(member, expiredTime);

            CreateAccessTokenRequest req = new CreateAccessTokenRequest(refreshToken);

            //when then
            assertThatThrownBy(() -> authService.createNewAccessToken(req))
                .isInstanceOf(BadCredentialsException.class);
        }
    }

    @Nested
    @DisplayName("로그아웃 요청 시 ")
    class Logout {

        @DisplayName("access 토큰이 유효하면 로그아웃한다.")
        @Test
        void success() {
            //given
            Member member = Member.builder()
                .name(MEMBER_NAME)
                .password(passwordEncoder.encode(MEMBER_NAME))
                .email(MEMBER_NAME + "@test.com")
                .isEnabled(true)
                .authority(Authority.ROLE_USER)
                .build();
            memberRepository.save(member);

            Date expiredTime = jwtProvider.calAccessTokenExpirationTime(LocalDateTime.now());
            String accessToken = jwtProvider.createToken(member, expiredTime);

            //when
            authService.logout(accessToken);

            //then
            assertThat(tokenRepository.exists(new RefreshToken(member.getName()))).isFalse();
            assertThat(tokenRepository.exists(new AccessToken(member.getName()))).isTrue();
        }
    }
}
