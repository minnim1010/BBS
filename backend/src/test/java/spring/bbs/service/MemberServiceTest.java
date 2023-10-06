package spring.bbs.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.IntegrationTestConfig;
import spring.bbs.auth.controller.dto.response.UserInfoResponse;
import spring.bbs.common.exceptionhandling.exception.DataNotFoundException;
import spring.bbs.common.exceptionhandling.exception.ExistedMemberNameException;
import spring.bbs.common.exceptionhandling.exception.NotSamePasswordException;
import spring.bbs.member.controller.dto.JoinRequest;
import spring.bbs.member.controller.dto.JoinResponse;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.member.service.MemberService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceTest extends IntegrationTestConfig {

    private static final String MEMBER_NAME = "MemberTestUser";
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("회원 가입 요청이 들어오면 ")
    class CreateMember {

        @Test
        @DisplayName("회원을 생성하고 생성된 회원 정보를 반환한다.")
        void returnResponse() {
            //given
            JoinRequest request = new JoinRequest(
                MEMBER_NAME,
                MEMBER_NAME,
                MEMBER_NAME,
                MEMBER_NAME + "@test.com");

            //when
            JoinResponse response = memberService.createMember(request);

            //then
            assertThat(response)
                .extracting("name", "email")
                .contains(MEMBER_NAME, MEMBER_NAME + "@test.com");

            List<Member> result = memberRepository.findAll();
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("두 개의 비밀번호가 같지 않다면 회원을 생성하지 않는다.")
        void failWithNotSamePassword() {
            //given
            JoinRequest request = new JoinRequest(
                MEMBER_NAME,
                MEMBER_NAME,
                "invalidPassword",
                MEMBER_NAME + "@test.com");

            //when then
            assertThatThrownBy(() ->
                memberService.createMember(request))
                .isInstanceOf(NotSamePasswordException.class);
        }

        @Test
        @DisplayName("회원 이름이 이미 존재한다면 회원을 생성하지 않는다.")
        void failWithDuplicatedName() {
            //given
            Member member = createMember(MEMBER_NAME);

            JoinRequest request = new JoinRequest(
                MEMBER_NAME,
                MEMBER_NAME,
                MEMBER_NAME,
                MEMBER_NAME + "@test.com");

            //when then
            assertThatThrownBy(() ->
                memberService.createMember(request))
                .isInstanceOf(ExistedMemberNameException.class);
        }
    }

    @Nested
    @DisplayName("회원 삭제 요청 시")
    class DeleteMember {
        @Test
        @DisplayName("회원을 삭제한다.")
        void success() {
            //given
            createMember(MEMBER_NAME);

            //when
            memberService.deleteMember(MEMBER_NAME);

            //then
            List<Member> result = memberRepository.findAll();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 회원 이름이라면 회원을 삭제할 수 없다.")
        void failWithNonExistMember() {
            //given
            //when then
            assertThatThrownBy(() ->
                memberService.deleteMember("invalidMember"))
                .isInstanceOf(DataNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("회원 정보 요청 시")
    class GetUserInfo {
        @DisplayName("회원 정보를 반환한다.")
        @Test
        void returnUserInfo() {
            //given
            Member member = createMember(MEMBER_NAME);

            //when
            UserInfoResponse userInfoResponse = memberService.get(MEMBER_NAME);

            //then
            assertThat(userInfoResponse).isNotNull()
                .extracting("username", "email")
                .contains(member.getName(), member.getEmail());
        }

        @DisplayName("존재하지 않는 회원 이름이라면 예외가 발생한다.")
        @Test
        void failWithNonExistMember() {
            //given

            //when then
            assertThatThrownBy(() -> memberService.get(MEMBER_NAME))
                .isInstanceOf(DataNotFoundException.class);
        }
    }

    private Member createMember(String name) {
        Member newMember = Member.builder()
            .name(name)
            .password(name)
            .email(name + "@test.com")
            .authority(Authority.ROLE_USER)
            .build();

        return memberRepository.save(newMember);
    }
}
