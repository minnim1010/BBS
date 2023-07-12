package spring.bbs.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.LoginResponse;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class IntegrationTests {

    private final Logger logger = LoggerFactory.getLogger(
            spring.bbs.jwt.IntegrationTests.class);

    private final String memberName = "jwttest";
    private final String memberRole = "ROLE_USER";

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtProvider jwtProvider;

    @PostConstruct
    private void createUser(){
        memberRepository.findByName(memberName)
                .ifPresent(m -> memberRepository.delete(m));

        Member member = new Member();
        member.setName(memberName);
        member.setPassword(passwordEncoder.encode(memberName));
        member.setEmail(memberName + "@test.com");
        member.setActivated(true);
        member.setAuthority(new Authority("ROLE_USER"));
        Member savedMember = memberRepository.save(member);

        logger.debug("saved member: {}", savedMember.getName());
    }

    @Test
    public void givenValidAccount_thenReturnLoginToken() throws Exception {

        logger.debug("로그인 성공");

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
    public void givenWrongAccount_thenUnauthorizedError() throws Exception {

        logger.debug("로그인 실패");

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
    public void givenValidToken_thenTokenInvalid() throws Exception {

        logger.debug("로그아웃 성공");

        //given
        String token = getJwtToken();
        String tokenHeader = "Bearer " + token;
        logger.debug(token);
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader));
        //then
        response.andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/home"));

        assert(jwtProvider.isLogoutToken(token));
    }

    @Test
    public void givenInvalidToken_thenUnauthorizedError() throws Exception {

        logger.debug("토큰 인증 실패");

        //given
        StringBuilder tokenBuilder = new StringBuilder(getJwtToken());
        tokenBuilder.replace(0, 5, "9378");
        String token = tokenBuilder.toString();
        String tokenHeader = "Bearer " + token;
        logger.debug(token);
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader));
        //then
        response.andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private String getJwtToken(){
        Member member = new Member(memberName, "", "", true, new Authority(memberRole));
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        member, "", List.of(new SimpleGrantedAuthority(memberRole)));
        return jwtProvider.createToken(token);
    }
}
