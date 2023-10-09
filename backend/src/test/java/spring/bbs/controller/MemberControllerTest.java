//package spring.bbs.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import spring.bbs.common.exception.DuplicatedMemberNameException;
//import spring.bbs.common.exception.NotSamePasswordException;
//import spring.bbs.member.controller.MemberController;
//import spring.bbs.member.controller.dto.JoinRequest;
//import spring.bbs.member.controller.dto.JoinResponse;
//import spring.bbs.member.service.MemberService;
//import spring.profileResolver.ProfileConfiguration;
//
//import static org.hamcrest.Matchers.is;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.refEq;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = MemberController.class)
//@AutoConfigureMockMvc
//@ProfileConfiguration
//public class MemberControllerTest {
//    private static final String MEMBER_NAME = "MemberTestUser";
//
//    private static final String specificUrl = "/api/v1/members/{id}";
//    private static final String collectionUrl = "/api/v1/members";
//
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private MemberService memberService;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Nested
//    class 회원가입 {
//
//        private ResultActions request(JoinRequest req) throws Exception {
//            return mockMvc.perform(post(collectionUrl)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(req)));
//        }
//
//        @Test
//        @DisplayName("유효한 회원가입 정보여야 한다.")
//        void join() throws Exception {
//            //given
//            JoinRequest req = createJoinRequest();
//            Long newMemberId = 1L;
//            JoinResponse expectedResponse = JoinResponse.builder()
//                .id(newMemberId)
//                .name(req.getName())
//                .email(req.getEmail())
//                .build();
//
//            given(memberService.createMember(any(JoinRequest.class)))
//                .willReturn(expectedResponse);
//            //when
//            ResultActions response = request(req);
//            //then
//            response.andDo(print()).
//                andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(req.getName())))
//                .andExpect(jsonPath("$.email", is(req.getEmail())));
//            verify(memberService).createMember(refEq(req));
//        }
//
//        @Test
//        @DisplayName("회원 이름이 이미 존재하면 안된다.")
//        void joinWithDuplicatedName() throws Exception {
//            //given
//            JoinRequest req = createJoinRequest();
//            given(memberService.createMember(any(JoinRequest.class)))
//                .willThrow(DuplicatedMemberNameException.class);
//            //when
//            ResultActions response = request(req);
//            //then
//            response.andDo(print())
//                .andExpect(status().isConflict());
//            verify(memberService).createMember(refEq(req));
//        }
//
//        @Test
//        @DisplayName("두 비밀번호가 같지 않으면 안된다.")
//        void joinWithNotSamePasswords() throws Exception {
//            //given
//            JoinRequest req = createJoinRequest();
//            given(memberService.createMember(any(JoinRequest.class)))
//                .willThrow(NotSamePasswordException.class);
//            //when
//            ResultActions response = request(req);
//            //then
//            response.andDo(print())
//                .andExpect(status().isBadRequest());
//            verify(memberService).createMember(refEq(req));
//        }
//    }
//
//    private static JoinRequest createJoinRequest() {
//        return new JoinRequest(MEMBER_NAME,
//            MEMBER_NAME,
//            MEMBER_NAME,
//            MEMBER_NAME + "@test.com");
//    }
//}
