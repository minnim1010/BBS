package spring.bbs.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.AuthenticationTests;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.util.RoleType;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.bbs.member.dto.util.RequestToMember.convertRequestToMember;

@Slf4j
public class IntegrationTest extends AuthenticationTests {

    private final String JoinRequestPath
            = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/member/JoinRequest.json";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;

    private final String username = "memberTestUser";

    public IntegrationTest(){
        setMemberName(username);
    }

    @Test
    @DisplayName("회원가입 성공")
    public void givenNewMember_thenGetNewMember() throws Exception {
        //given
        JoinRequest req = objectMapper
                .readValue(new File(JoinRequestPath), JoinRequest.class);
        log.debug(req.toString());

        memberRepository.findByName(req.getName())
                .ifPresent(m -> memberRepository.delete(m));
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(req.getName())))
                .andExpect(jsonPath("$.email", is(req.getEmail())));
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    public void givenExistedMember_thenMemberDelete() throws Exception {
        //given
        JoinRequest req = objectMapper
                .readValue(new File(JoinRequestPath), JoinRequest.class);
        log.debug(req.toString());

        if(memberRepository.findByName(req.getName()).isEmpty())
            memberRepository.save(convertRequestToMember(req, RoleType.user, passwordEncoder.encode(req.getName())));

        String token = getJwtToken();
        String tokenHeader = getJwtTokenHeader(token);
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/members")
                .header(AUTHENTICATION_HEADER, tokenHeader));
        //then
        response.andDo(print()).
                andExpect(status().isOk());
        assert(memberRepository.findByName(req.getName()).isEmpty());
    }

    @Test
    @DisplayName("회원탈퇴 실패: 존재하지 않는 회원")
    public void givenExistedMember_thenDataNotFoundError() throws Exception {
        //given
        JoinRequest req = objectMapper
                .readValue(new File(JoinRequestPath), JoinRequest.class);
        log.debug(req.toString());

        memberRepository.findByName(req.getName()).ifPresent((m) ->
                memberRepository.delete(m));

        String token = getJwtToken();
        String tokenHeader = getJwtTokenHeader(token);
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/members")
                .header(AUTHENTICATION_HEADER, tokenHeader));
        //then
        response.andDo(print()).
                andExpect(status().isNotFound());
    }
}
