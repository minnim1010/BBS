package spring.bbs.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.bbs.exceptionhandler.exception.ExistedMemberNameException;
import spring.bbs.exceptionhandler.exception.NotSamePasswordException;
import spring.bbs.member.domain.Member;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.dto.response.JoinResponse;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.member.service.MemberService;
import spring.bbs.util.AuthenticationUtil;
import spring.bbs.util.RoleType;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static spring.bbs.member.dto.util.RequestToMember.convertRequestToMember;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ServiceTest {

    private final String JoinRequestPath
            = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/member/JoinRequest.json";

    private JoinRequest joinRequest;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    private static MockedStatic<AuthenticationUtil> securityUtil;

    @BeforeAll
    static void setup(){
        securityUtil = mockStatic(AuthenticationUtil.class);
    }

    @AfterAll
    static void teardown(){
        securityUtil.close();
    }

    private JoinRequest getJoinRequest(){
        if(joinRequest == null){
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
    @DisplayName("멤버 생성 성공")
    public void createUserSuccess(){
        //given
        JoinRequest req = getJoinRequest();
        String encodedPassword = "Encrypted";
        Member newMember = convertRequestToMember(req, RoleType.user, encodedPassword);
        Long newMemberId = 1L;
        newMember.setId(newMemberId);

        given(memberRepository.existsByName(newMember.getName()))
                .willReturn(false);
        given(memberRepository.save(any()))
                .willReturn(newMember);
        //when
        JoinResponse response = memberService.createMember(req);
        //then
        assertEquals(newMemberId, response.getId());
        assertEquals(newMember.getName(), response.getName());
        assertEquals(newMember.getEmail(), response.getEmail());
    }

    @Test
    @DisplayName("멤버 생성 실패: 패스워드 불일치")
    public void createUserFail_NotSamePassword(){
        //given
        JoinRequest req = getJoinRequest();
        req.setCheckPassword("NotSamePassword");
        //when & then
        assertThatThrownBy(() -> memberService.createMember(req)).isInstanceOf(NotSamePasswordException.class);
    }

    @Test
    @DisplayName("멤버 생성 실패: 존재하는 유저 이름")
    public void createUserFail_ExistedMemberName(){
        //given
        JoinRequest req = getJoinRequest();
        given(memberRepository.existsByName(req.getName()))
                .willReturn(true);
        //when & then
        assertThatThrownBy(() -> memberService.createMember(req)).isInstanceOf(ExistedMemberNameException.class);
    }

    @Test
    @DisplayName("멤버 삭제 성공")
    public void deleteUserSuccess(){
        //given
        JoinRequest req = getJoinRequest();
        String encodedPassword = "Encrypted";
        Member newMember = convertRequestToMember(req, RoleType.user, encodedPassword);
        Long newMemberId = 1L;
        newMember.setId(newMemberId);

        given(AuthenticationUtil.getCurrentUsername())
                .willReturn(Optional.of(joinRequest.getName()));
        given(memberRepository.findByName(anyString())).willReturn(Optional.of(newMember));
        doNothing().when(memberRepository).delete(any(Member.class));
        //when
        memberService.deleteMember();
        //then
        verify(memberRepository).findByName(newMember.getName());
        verify(memberRepository).delete(newMember);
        securityUtil.verify(AuthenticationUtil::getCurrentUsername);
    }
}
