package spring.bbs.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.AuthenticationTests;
import spring.bbs.jwt.dto.request.CreateAccessTokenRequest;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.AccessTokenResponse;
import spring.bbs.jwt.dto.response.LoginResponse;
import spring.bbs.jwt.repository.TokenRepository;
import spring.bbs.util.RoleType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class JwtIntegrationTests extends AuthenticationTests {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenRepository tokenRepository;

    private final String username = "JwtTestUser";

    public JwtIntegrationTests(){
        setMemberName(username);
    }

    @Nested
    class Login{

        private ResultActions request(LoginRequest req) throws Exception {
            return mockMvc.perform(post("/api/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)));
        }

        @Test
        @DisplayName("유효한 아이디와 비밀번호를 입력하면 로그인할 수 있다.")
        public void login() throws Exception {
            //given
            LoginRequest req = new LoginRequest(memberName, memberName);
            //when
            ResultActions response = request(req);
            //then
            MvcResult result = response.andExpect(status().isOk()).andReturn();
            LoginResponse body = objectMapper.readValue(
                    result.getResponse().getContentAsString(), LoginResponse.class);
            assert(jwtProvider.isValidToken(body.getAccessToken()));
            assert(jwtProvider.isValidToken(body.getRefreshToken()));
        }

        @Test
        @DisplayName("틀린 아이디와 비밀번호를 입력하면 로그인할 수 없다.")
        public void loginWithWrontAccount() throws Exception {
            //given
            LoginRequest req = new LoginRequest("wrongAccount", "wrongAccount");
            //when
            ResultActions response = request(req);
            //then
            response.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class CreateNewAccessToken{

        private ResultActions request(CreateAccessTokenRequest req) throws Exception {
            return mockMvc.perform(post("/api/v1/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)));
        }

        @Test
        @DisplayName("Refresh token이 유효하면 새로운 액세스 토큰을 발급한다.")
        public void createNewAccessToken() throws Exception {
            //given
            String refreshToken = generateRefreshToken();
            tokenRepository.saveRefreshToken(refreshToken, jwtProvider.getExpiration(refreshToken));
            CreateAccessTokenRequest req = new CreateAccessTokenRequest(refreshToken);
            //when
            ResultActions response = request(req);
            //then
            MvcResult result = response.andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            AccessTokenResponse body = objectMapper.readValue(result.getResponse().getContentAsString(), AccessTokenResponse.class);
            assert(jwtProvider.isValidToken(body.getToken()));
        }

        @Test
        @DisplayName("Refresh token이 만료되었으면 액세스 토큰을 발급하지 않는다.")
        public void createNewAccessTokenWithExpiredRefreshToken() throws Exception {
            //given
            CreateAccessTokenRequest req = new CreateAccessTokenRequest(generateRefreshToken());
            //when
            ResultActions response = request(req);
            //then
            response.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Refresh token의 형식이 올바르지 않으면 액세스 토큰을 발급하지 않는다.")
        public void createNewAccessTokenWithMalformedRefreshToken() throws Exception {
            //given
            String refreshToken = "invalid-token";
            tokenRepository.saveRefreshToken(refreshToken, 10000000);
            CreateAccessTokenRequest req = new CreateAccessTokenRequest(refreshToken);

            //when
            ResultActions response = request(req);
            //then
            response.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class Logout{

        private ResultActions request(String tokenHeader) throws Exception{
            return mockMvc.perform(get("/api/v1/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHENTICATION_HEADER, tokenHeader));
        }

        @Test
        @DisplayName("유효한 액세스 토큰이면 로그아웃한다.")
        public void logout() throws Exception {
            //given
            String token = getJwtToken();
            String tokenHeader = getJwtTokenHeader(token);
            //when
            ResultActions response = request(tokenHeader);
            //then
            response.andExpect(status().isOk());

            assert(jwtProvider.isLogoutAccessToken(token));
        }

        @Test
        @DisplayName("잘못된 형식의 액세스 토큰이면 로그아웃하지 않는다.")
        public void logoutWithMalformedAccessToken() throws Exception {
            //given
            String token = "invalid token";
            String tokenHeader = getJwtTokenHeader(token);
            //when
            ResultActions response = request(tokenHeader);
            //then
            response.andExpect(status().isUnauthorized());
        }
    }

    private String generateRefreshToken() {
        UserDetails userDetails = generateTestUser();
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, List.of(new SimpleGrantedAuthority(RoleType.user)));
        return jwtProvider.generateRefreshToken(authentication);
    }

    private UserDetails generateTestUser(){
        return new User(memberName, memberName,
                List.of(new SimpleGrantedAuthority(RoleType.user)));
    }
}
