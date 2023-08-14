package spring.bbs.member.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.member.domain.Member;

@RequiredArgsConstructor
@Component
public class MemberRepositoryHandler {
    private static final String MEMBER_NOT_FOUND_MSG = "회원을 찾을 수 없습니다.";

    private final MemberRepository memberRepository;

    public Member findByName(String authorName){
        return memberRepository.findByName(authorName).orElseThrow(
                () -> new DataNotFoundException(MEMBER_NOT_FOUND_MSG));
    }

    public Member findById(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(
                () -> new DataNotFoundException(MEMBER_NOT_FOUND_MSG));
    }

    public Member findByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow(
                () -> new DataNotFoundException(MEMBER_NOT_FOUND_MSG));
    }
}
