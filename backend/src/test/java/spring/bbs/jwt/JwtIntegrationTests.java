package spring.bbs.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
import spring.bbs.jwt.repository.RefreshToken;
import spring.bbs.jwt.repository.RefreshTokenRepository;
import spring.bbs.member.RoleType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JwtIntegrationTests extends AuthenticationTests {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private final String username = "JwtTestUser";

    public JwtIntegrationTests(){
        setMemberName(username);
    }

    @Test
    @DisplayName("로그인 성공")
    public void givenValidAccount_thenReturnRefreshAndAccessToken() throws Exception {
        //given
        LoginRequest req = new LoginRequest(memberName, memberName);
        logger.debug(req.toString());
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        MvcResult result = response.andDo(print())
                        .andExpect(status().isOk())
                .andReturn();

        LoginResponse body = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        assert(jwtProvider.isValidToken(body.getAccessToken()));
        assert(jwtProvider.isValidToken(body.getRefreshToken()));
    }

    @Test
    @DisplayName("로그인 실패: 틀린 계정 정보")
    public void givenWrongAccount_thenUnauthorizedError() throws Exception {
        //given
        LoginRequest req = new LoginRequest("wrongAccount", "wrongAccount");
        logger.debug(req.toString());
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("새 토큰 발급 성공")
    public void givenRefreshToken_thenReturnNewAccessToken() throws Exception {
        //given
        String refreshToken = generateRefreshToken();
        long expiration = jwtProvider.getExpiration(refreshToken);
        refreshTokenRepository.save(new RefreshToken(testMember.getId(), refreshToken));

        CreateAccessTokenRequest req = new CreateAccessTokenRequest(refreshToken);
        logger.debug(req.toString());
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        MvcResult result = response.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        AccessTokenResponse body = objectMapper.readValue(result.getResponse().getContentAsString(), AccessTokenResponse.class);
        assert(jwtProvider.isValidToken(body.getToken()));
    }

    @Test
    @DisplayName("새 토큰 발급 실패: 만료된 토큰")
    public void givenRefreshToken_thenUnauthorizedError1() throws Exception {
        //given
        CreateAccessTokenRequest req = new CreateAccessTokenRequest(generateRefreshToken());
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("새 토큰 발급 실패: 잘못된 형식을 가진 토큰")
    public void givenRefreshToken_thenUnauthorizedError2() throws Exception {
        //given
        String refreshToken = "invalid-token";

        CreateAccessTokenRequest req = new CreateAccessTokenRequest(refreshToken);
        logger.debug(req.toString());
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그아웃 성공")
    public void givenValidToken_thenTokenInvalid() throws Exception {
        //given
        String token = getJwtToken();
        String tokenHeader = getJwtTokenHeader(token);
        logger.debug(token);
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHENTICATION_HEADER, tokenHeader));
        //then
        response.andDo(print())
                .andExpect(status().isOk());

        assert(jwtProvider.isLogoutAccessToken(token));
    }

    @Test
    @DisplayName("로그아웃 실패: 유효하지 않은 토큰")
    public void givenInvalidToken_thenUnauthorizedError() throws Exception {
        //given
        String token = "invalid token";
        String tokenHeader = getJwtTokenHeader(token);
        logger.debug(token);
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHENTICATION_HEADER, tokenHeader));
        //then
        response.andDo(print())
                .andExpect(status().isUnauthorized());
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
