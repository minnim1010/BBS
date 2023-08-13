package spring.bbs.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
    class 로그인{

        private ResultActions request(LoginRequest req) throws Exception {
            return mockMvc.perform(post("/api/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)));
        }

        @Test
        public void 올바른계정정보면_성공() throws Exception {
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
        public void 틀린계정정보면_Unauthorized() throws Exception {
            //given
            LoginRequest req = new LoginRequest("wrongAccount", "wrongAccount");
            //when
            ResultActions response = request(req);
            //then
            response.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class 새로운액세스토큰_발급요청{

        private ResultActions request(CreateAccessTokenRequest req) throws Exception {
            return mockMvc.perform(post("/api/v1/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)));
        }

        @Test
        public void 유효한RefreshToken이면_성공_새로운액세스토큰발급() throws Exception {
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
        public void 만료된RefreshToken이면_Unauthorized() throws Exception {
            //given
            CreateAccessTokenRequest req = new CreateAccessTokenRequest(generateRefreshToken());
            //when
            ResultActions response = request(req);
            //then
            response.andExpect(status().isUnauthorized());
        }

        @Test
        public void 잘못된형식의RefreshToken이면_Unauthorized() throws Exception {
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
    class 로그아웃{

        private ResultActions request(String tokenHeader) throws Exception{
            return mockMvc.perform(get("/api/v1/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHENTICATION_HEADER, tokenHeader));
        }

        @Test
        public void 유효한AccessToken이면_성공_AccessToken을저장() throws Exception {
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
        public void 잘못된형식의AccessToken이면_Unauthorized() throws Exception {
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
