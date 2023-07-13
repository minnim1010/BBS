package spring.bbs.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.AuthenticationTests;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.LoginResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JwtIntegrationTests extends AuthenticationTests {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtProvider jwtProvider;

    private final String username = "JwtTest";

    public JwtIntegrationTests(){
        setMemberName(username);
    }

    @Test
    @DisplayName("로그인 성공")
    public void givenValidAccount_thenReturnLoginToken() throws Exception {
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
        assert(jwtProvider.isValidToken(body.getToken()));
        logger.debug("token is valid");
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
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/home"));

        assert(jwtProvider.isLogoutToken(token));
    }

    @Test
    @DisplayName("토큰 인증 실패: 유효하지 않은 토큰")
    public void givenInvalidToken_thenUnauthorizedError() throws Exception {
        //given
        StringBuilder tokenBuilder = new StringBuilder(getJwtToken());
        tokenBuilder.replace(0, 5, "9378");
        String token = tokenBuilder.toString();
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

}
