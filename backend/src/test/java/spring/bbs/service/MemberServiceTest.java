package spring.bbs.service;//package spring.bbs.member.test;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//import spring.bbs.ProfileConfiguration;
//import spring.bbs.exceptionhandler.exception.ExistedMemberNameException;
//import spring.bbs.exceptionhandler.exception.NotSamePasswordException;
//import spring.bbs.member.domain.Authority;
//import spring.bbs.member.domain.Member;
//import spring.bbs.member.dto.request.JoinRequest;
//import spring.bbs.member.dto.response.JoinResponse;
//import spring.bbs.member.repository.MemberRepository;
//import spring.bbs.member.service.MemberService;
//import spring.bbs.util.AuthenticationUtil;
//import spring.bbs.util.RoleType;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//import static spring.bbs.member.dto.util.RequestToMember.convertJoinRequestToMember;
//
//@ExtendWith(MockitoExtension.class)
//@Slf4j
//public class MemberServiceTest implements ProfileConfiguration {
//    @InjectMocks
//    private MemberService memberService;
//    @Mock
//    private MemberRepository memberRepository;
//    private static MockedStatic<AuthenticationUtil> securityUtil;
//
//    @BeforeAll
//    static void setup(){
//        securityUtil = mockStatic(AuthenticationUtil.class);
//    }
//
//    @AfterAll
//    static void teardown(){
//        securityUtil.close();
//    }
//
//    @Nested
//    class CreateMember{
//        @Test
//        @DisplayName("")
//        public void createMember(){
//            //given
//            JoinRequest req = createJoinRequest();
//            String encodedPassword = "Encrypted";
//
//            given(memberRepository.existsByName(req.getName()))
//                    .willReturn(false);
//            given(memberRepository.save(any()))
//                    .willReturn(newMember);
//            //when
//            JoinResponse response = memberService.createMember(req);
//            //then
//            assertEquals(newMemberId, response.getId());
//            assertEquals(newMember.getName(), response.getName());
//            assertEquals(newMember.getEmail(), response.getEmail());
//        }
//
//        @Test
//        public void createMemberWithNotSamePassword(){
//            //given
//            JoinRequest req = createJoinRequest();
//            req.setCheckPassword("NotSamePassword");
//            //when & then
//            assertThatThrownBy(() ->
//                    memberService.createMember(req)).isInstanceOf(NotSamePasswordException.class);
//        }
//
//        @Test
//        public void createMemberWithDuplicatedName(){
//            //given
//            JoinRequest req = createJoinRequest();
//            given(memberRepository.existsByName(req.getName()))
//                    .willReturn(true);
//            //when & then
//            assertThatThrownBy(() ->
//                    memberService.createMember(req)).isInstanceOf(ExistedMemberNameException.class);
//        }
//    }
//
//    @Nested
//    class 회원삭제{
//        @Test
//        @DisplayName("멤버 삭제 성공")
//        public void deleteUserSuccess(){
//            //given
//            JoinRequest req = createJoinRequest();
//            Member newMember = convertJoinRequestToMember(req, RoleType.user, "Encrypted");
//            Long newMemberId = 1L;
//            newMember.setId(newMemberId);
//
//            given(memberRepository.findByName(anyString())).willReturn(Optional.of(newMember));
//            doNothing().when(memberRepository).delete(any(Member.class));
//            //when
//            memberService.deleteMember(req.getName());
//            //then
//            verify(memberRepository).findByName(newMember.getName());
//            verify(memberRepository).delete(newMember);
//        }
//    }
//
//    private static JoinRequest createJoinRequest() {
//        return new JoinRequest("memberTestUser",
//            "memberTestUser",
//            "memberTestUser",
//            "memberTest@test.com");
//    }
//}
