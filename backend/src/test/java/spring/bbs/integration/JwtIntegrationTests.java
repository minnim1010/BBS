//package spring.bbs.integration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.PostConstruct;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import spring.bbs.auth.controller.dto.request.CreateAccessTokenRequest;
//import spring.bbs.auth.controller.dto.request.LoginRequest;
//import spring.bbs.auth.controller.dto.response.AccessTokenResponse;
//import spring.bbs.auth.controller.dto.response.LoginResponse;
//import spring.bbs.auth.domain.RefreshToken;
//import spring.bbs.auth.domain.Token;
//import spring.bbs.auth.repository.TokenRepository;
//import spring.bbs.common.jwt.JwtProvider;
//import spring.bbs.member.domain.Authority;
//import spring.bbs.member.domain.Member;
//import spring.bbs.member.repository.MemberRepository;
//import spring.helper.AccessTokenProvider;
//import spring.helper.MemberCreator;
//import spring.profileResolver.ProfileConfiguration;
//
//import java.time.LocalDateTime;
//import java.util.Date;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@ProfileConfiguration
//public class JwtIntegrationTests {
//
//    private static final String MEMBER_NAME = "JwtTestUser";
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private TokenRepository tokenRepository;
//    @Autowired
//    private JwtProvider jwtProvider;
//    @Autowired
//    private MemberRepository memberRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    private AccessTokenProvider accessTokenProvider;
//    private MemberCreator memberCreator;
//
//    @PostConstruct
//    void init() {
//        accessTokenProvider = new AccessTokenProvider(jwtProvider, MEMBER_NAME);
//        memberCreator = new MemberCreator(memberRepository);
//    }
//
//    @AfterEach
//    void setUp() {
//        memberRepository.deleteAllInBatch();
//    }
//
//    @Nested
//    class Login {
//
//        private ResultActions request(LoginRequest req) throws Exception {
//            return mockMvc.perform(post("/api/v1/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(req)));
//        }
//
//        @Test
//        @DisplayName("유효한 아이디와 비밀번호를 입력하면 로그인할 수 있다.")
//        public void login() throws Exception {
//            //given
//            memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
//            LoginRequest req = new LoginRequest(MEMBER_NAME, MEMBER_NAME);
//            //when
//            ResultActions response = request(req);
//            //then
//            MvcResult result = response.andExpect(status().isOk()).andReturn();
//            LoginResponse body = objectMapper.readValue(
//                result.getResponse().getContentAsString(), LoginResponse.class);
//            assert (jwtProvider.isValidToken(body.getAccessToken()));
//            assert (jwtProvider.isValidToken(body.getRefreshToken()));
//        }
//
//        @Test
//        @DisplayName("틀린 아이디와 비밀번호를 입력하면 로그인할 수 없다.")
//        public void loginWithWrontAccount() throws Exception {
//            //given
//            memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
//            LoginRequest req = new LoginRequest("wrongAccount", "wrongAccount");
//            //when
//            ResultActions response = request(req);
//            //then
//            response.andExpect(status().isUnauthorized());
//        }
//    }
//
//    @Nested
//    class CreateNewAccessToken {
//
//        private ResultActions request(CreateAccessTokenRequest req) throws Exception {
//            return mockMvc.perform(post("/api/v1/token")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(req)));
//        }
//
//        @Test
//        @DisplayName("Refresh token이 유효하면 새로운 액세스 토큰을 발급한다.")
//        public void createNewAccessToken() throws Exception {
//            //given
//            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
//            Token refreshToken = createAndSaveRefreshToken(member);
//            CreateAccessTokenRequest req = new CreateAccessTokenRequest(refreshToken.getToken());
//            //when
//            ResultActions response = request(req);
//            //then
//            MvcResult result = response.andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//            AccessTokenResponse body = objectMapper.readValue(result.getResponse().getContentAsString(), AccessTokenResponse.class);
//            String token = body.getToken();
//            assertThat(jwtProvider.isValidToken(token)).isTrue();
//        }
//
//        @Test
//        @DisplayName("Refresh token이 만료되었으면 액세스 토큰을 발급하지 않는다.")
//        public void createNewAccessTokenWithExpiredRefreshToken() throws Exception {
//            //given
//            memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
//            CreateAccessTokenRequest req = new CreateAccessTokenRequest(generateRefreshToken());
//            //when
//            ResultActions response = request(req);
//            //then
//            response.andExpect(status().isUnauthorized());
//        }
//
//        @Test
//        @DisplayName("Refresh token의 형식이 올바르지 않으면 액세스 토큰을 발급하지 않는다.")
//        public void createNewAccessTokenWithMalformedRefreshToken() throws Exception {
//            //given
//            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
//            String refreshToken = "invalid-token";
//            tokenRepository.save(new RefreshToken(member.getName(), refreshToken), jwtProvider.getExpiration(refreshToken));
//            CreateAccessTokenRequest req = new CreateAccessTokenRequest(refreshToken);
//
//            //when
//            ResultActions response = request(req);
//            //then
//            response.andExpect(status().isUnauthorized());
//        }
//    }
//
//    @Nested
//    class Logout {
//
//        private ResultActions request(String tokenHeader) throws Exception {
//            return mockMvc.perform(post("/api/v1/logout")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(accessTokenProvider.AUTHENTICATION_HEADER, tokenHeader));
//        }
//
//        @Test
//        @DisplayName("유효한 액세스 토큰이면 로그아웃한다.")
//        public void logout() throws Exception {
//            //given
//            memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
//            String token = accessTokenProvider.createAccessToken();
//            String tokenHeader = accessTokenProvider.getUserRoleTokenWithHeaderPrefix(token);
//            //when
//            ResultActions response = request(tokenHeader);
//            //then
//            response.andExpect(status().isOk());
//
//            assert (jwtProvider.isLogoutAccessToken(token));
//        }
//    }
//
//    private Token createAndSaveRefreshToken(Member member) {
//        Date expiredTime = jwtProvider.calRefreshTokenExpiredTime(LocalDateTime.now());
//        String refreshToken = jwtProvider.createToken(member, expiredTime);
//
//        long timeout = expiredTime.getTime() - System.currentTimeMillis();
//        return tokenRepository.save(new RefreshToken(MEMBER_NAME, refreshToken), timeout);
//    }
//
//    private UserDetails createAndSaveUser() {
//        return new User(MEMBER_NAME, MEMBER_NAME,
//            List.of(new SimpleGrantedAuthority(Authority.ROLE_USER.name())));
//    }
//}
