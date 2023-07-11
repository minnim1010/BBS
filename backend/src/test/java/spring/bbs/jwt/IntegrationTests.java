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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.LoginResponse;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class IntegrationTests {

    private final Logger logger = LoggerFactory.getLogger(
            spring.bbs.jwt.IntegrationTests.class);

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
    public void createUser(){
        memberRepository.findByName("jwttest")
                .ifPresent(m -> memberRepository.delete(m));

        Member member = new Member();
        member.setName("jwttest");
        member.setPassword(passwordEncoder.encode("jwttest"));
        member.setEmail("jwttest");
        member.setActivated(true);
        member.setAuthority(new Authority("ROLE_USER"));
        Member savedMember = memberRepository.save(member);

        logger.debug("saved member: {}", savedMember.getName());
    }

    @Test
    public void login() throws Exception {
        LoginRequest req = new LoginRequest("jwttest", "jwttest");
        logger.debug(req.toString());

        ResultActions response = mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        MvcResult result = response.andDo(print())
                        .andExpect(status().isOk())
                .andReturn();

        LoginResponse body = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        assert(jwtProvider.isValidToken(body.getToken()));
        logger.debug("token is valid");
    }
}
