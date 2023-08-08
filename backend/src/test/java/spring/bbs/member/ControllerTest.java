package spring.bbs.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.config.security.SecurityConfig;
import spring.bbs.exceptionhandler.exception.ExistedMemberNameException;
import spring.bbs.exceptionhandler.exception.NotSamePasswordException;
import spring.bbs.member.controller.MemberController;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.dto.response.JoinResponse;
import spring.bbs.member.service.MemberService;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class})},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class})
@AutoConfigureMockMvc
public class ControllerTest {

    private final String JoinRequestPath
            = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/member/JoinRequest.json";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    private JoinRequest joinRequest;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private JoinRequest getJoinRequest() {
        if (joinRequest == null) {
            try {
                joinRequest = objectMapper
                        .readValue(new File(JoinRequestPath), JoinRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return joinRequest;
    }

    @Test
    @DisplayName("회원 가입 성공")
    void join_success() throws Exception {
        //given
        JoinRequest request = getJoinRequest();
        Long newMemberId = 1L;
        JoinResponse expectedResponse = new JoinResponse(newMemberId, request.getName(), request.getEmail());

        given(memberService.createMember(any(JoinRequest.class)))
                .willReturn(expectedResponse);
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.email", is(request.getEmail())));
        verify(memberService).createMember(refEq(request));
    }

    @Test
    @DisplayName("회원 가입 실패: 존재하는 멤버 이름")
    void join_fail_existedMemberName() throws Exception {
        //given
        JoinRequest request = getJoinRequest();
        given(memberService.createMember(any(JoinRequest.class)))
                .willThrow(ExistedMemberNameException.class);
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        response.andDo(print())
                .andExpect(status().isConflict());
        verify(memberService).createMember(refEq(request));
    }

    @Test
    @DisplayName("회원 가입 실패: 패스워드 불일치")
    void join_fail_NotSamePassword() throws Exception {
        //given
        JoinRequest request = getJoinRequest();
        given(memberService.createMember(any(JoinRequest.class)))
                .willThrow(NotSamePasswordException.class);
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        response.andDo(print())
                .andExpect(status().isUnprocessableEntity());
        verify(memberService).createMember(refEq(request));
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdrawal_success() throws Exception {
        //given
        doNothing().when(memberService).deleteMember();
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/members"));
        //then
        response.andDo(print())
                .andExpect(status().isOk());
        verify(memberService).deleteMember();
    }
}
