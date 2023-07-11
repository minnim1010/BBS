package spring.bbs.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.repository.MemberRepository;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class IntegrationTests {

    private final String CreateMemberDataPath
            = "/Users/mjmj/Desktop/bbs/backend/src/test/java/spring/bbs/member/CreateMemberData.json";
    private final Logger logger = LoggerFactory.getLogger(
            IntegrationTests.class);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){

    }

    @Test
    public void joinTest() throws Exception{
        JoinRequest req = objectMapper
                .readValue(new File(CreateMemberDataPath), JoinRequest.class);
        logger.debug(req.toString());

        memberRepository.findByName(req.getName())
                .ifPresent(m -> memberRepository.delete(m));

        ResultActions response = mockMvc.perform(post("/api/v1/join")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(req)));

        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(req.getName())))
                .andExpect(jsonPath("$.email", is(req.getEmail())));
    }
}
