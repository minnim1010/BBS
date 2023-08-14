package spring.bbs.member.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.AuthenticationTests;
import spring.bbs.member.domain.Member;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.repository.MemberRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class MemberIntegrationTest extends AuthenticationTests {

    private final String specificUrl = "/api/v1/members/{id}";
    private final String collectionUrl = "/api/v1/members";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;

    private final String username = "memberTestUser";

    public MemberIntegrationTest(){
        setMemberName(username);
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @BeforeEach
    void setUp() {
        memberRepository.deleteAllInBatch();
    }

    @Nested
    class Join{

        private ResultActions request(JoinRequest req) throws Exception {
            return mockMvc.perform(post(collectionUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)));
        }

        @Test
        @DisplayName("유효한 회원가입 정보면 회원가입할 수 있다.")
        public void join() throws Exception {
            //given
            JoinRequest req = createJoinRequest();
            //when
            ResultActions response = request(req);
            //then
            response.andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(req.getName())))
            .andExpect(jsonPath("$.email", is(req.getEmail())));
        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(1)
                .extracting("name", "email")
                .contains(Tuple.tuple("memberTestUser", "memberTest@test.com"));
    }

        @Test
        @DisplayName("회원 이름이 이미 존재하면 회원가입할 수 없다.")
        public void joinWithDuplicatedName() throws Exception {
            //given
            JoinRequest req = createJoinRequest();
            memberRepository.findByName(req.getName())
                    .orElse(createMember(req.getName()));
            //when
            ResultActions response = request(req);
            //then
            response.andExpect(status().isConflict());
        }

        @Test
        @DisplayName("두 비밀번호가 같지 않으면 회원가입할 수 없다.")
        public void joinWithNotSamePasswords() throws Exception {
            //given
            JoinRequest req = createJoinRequest();
            req.setCheckPassword("notSamePassword");
            //when
            ResultActions response = request(req);
            //then
            response.andExpect(status().isBadRequest());
        }
    }

    @Nested
    class Withdrawal{

        private ResultActions request(String tokenHeader) throws Exception {
            return mockMvc.perform(delete(collectionUrl)
                    .header(AUTHENTICATION_HEADER, tokenHeader));
        }

        @Test
        @DisplayName("로그인한 회원이라면 회원을 탈퇴할 수 있다.")
        public void withdrawal() throws Exception {
            //given
            JoinRequest req = createJoinRequest();
            memberRepository.findByName(req.getName())
                    .orElse(createMember(req.getName()));
            //when
            ResultActions response = request(getJwtTokenHeader(getJwtToken()));
            //then
            response.andExpect(status().isOk());
            assertThat(memberRepository.findAll()).isEmpty();
        }
    }

    private static JoinRequest createJoinRequest() {
        return new JoinRequest("memberTestUser",
            "memberTestUser",
            "memberTestUser",
            "memberTest@test.com");
    }
}
