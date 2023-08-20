package spring.bbs.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.bbs.common.exception.DataNotFoundException;
import spring.bbs.common.exception.ExistedMemberNameException;
import spring.bbs.common.exception.NotSamePasswordException;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.dto.response.JoinResponse;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.member.service.MemberService;
import spring.bbs.post.repository.PostRepository;
import spring.profileResolver.ProfileConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
@ProfileConfiguration
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MemberServiceTest {

    private static final String MEMBER_NAME = "MemberTestUser";
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PostRepository postRepository;

    @Nested
    @DisplayName("회원 가입 요청이 들어오면 ")
    class CreateMember {
        @Nested
        @DisplayName("회원을 생성하고 ")
        class success {
            @Test
            @DisplayName("생성된 회원 정보를 반환한다.")
            public void returnResponse() {
                //given
                JoinRequest req = createJoinRequest();
                Long id = 1L;
                Member newMember = createMember(id);

                given(memberRepository.existsByName(req.getName()))
                    .willReturn(false);
                given(memberRepository.save(any(Member.class)))
                    .willReturn(newMember);
                given(passwordEncoder.encode(anyString()))
                    .willReturn(newMember.getPassword());
                //when
                JoinResponse response = memberService.createMember(req);
                //then
                assertThat(response)
                    .extracting("id", "name", "email")
                    .contains(newMember.getId(), newMember.getName(), newMember.getEmail());
            }
        }

        @Test
        @DisplayName("두 개의 비밀번호가 같지 않다면 회원을 생성하지 않는다.")
        public void failWithNotSamePassword() {
            //given
            JoinRequest req = createJoinRequest();
            req.setCheckPassword("NotSamePassword");
            //when & then
            assertThatThrownBy(() ->
                memberService.createMember(req))
                .isInstanceOf(NotSamePasswordException.class);
        }

        @Test
        @DisplayName("회원 이름이 이미 존재한다면 회원을 생성하지 않는다.")
        public void failWithDuplicatedName() {
            //given
            JoinRequest req = createJoinRequest();
            given(memberRepository.existsByName(req.getName()))
                .willReturn(true);
            //when & then
            assertThatThrownBy(() ->
                memberService.createMember(req))
                .isInstanceOf(ExistedMemberNameException.class);
        }
    }

    @Nested
    @DisplayName("회원 삭제 요청 시")
    class DeleteMember {
        @Test
        @DisplayName("회원을 삭제한다.")
        public void success() {
            //given
            JoinRequest req = createJoinRequest();
            Long newMemberId = 1L;
            Member newMember = createMember(newMemberId);

            given(memberRepository.findByName(newMember.getName()))
                .willReturn(Optional.of(newMember));
            doNothing().when(postRepository).deleteAllInBatchByAuthor(any(Member.class));
            doNothing().when(memberRepository).delete(any(Member.class));
            //when
            memberService.deleteMember(req.getName());
            //then
            verify(memberRepository).findByName(newMember.getName());
            verify(postRepository).deleteAllInBatchByAuthor(any(Member.class));
            verify(memberRepository).delete(newMember);
        }

        @Test
        @DisplayName("존재하지 않는 회원 이름이라면 회원을 삭제할 수 없다.")
        void failWithNonExistMember() {
            //given
            JoinRequest req = createJoinRequest();
            Long newMemberId = 1L;
            Member newMember = createMember(newMemberId);

            //when
            assertThatThrownBy(() ->
                memberService.deleteMember(req.getName()))
                .isInstanceOf(DataNotFoundException.class);
            //then
            verify(memberRepository).findByName(newMember.getName());
        }
    }

    private JoinRequest createJoinRequest() {
        return new JoinRequest(MEMBER_NAME,
            MEMBER_NAME,
            MEMBER_NAME,
            MEMBER_NAME + "@test.com");
    }

    private Member createMember(Long id) {
        return Member.builder()
            .id(id)
            .name(MEMBER_NAME)
            .password(MEMBER_NAME)
            .authority(Authority.ROLE_USER)
            .email(MEMBER_NAME + "@test.com")
            .build();
    }
}
