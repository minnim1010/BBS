package spring.bbs.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

@RequiredArgsConstructor
@Component
public class MemberUtil {
    private final MemberRepository memberRepository;

    public Member findMemberByName(String authorName){
        return memberRepository.findByName(authorName).orElseThrow(
                () -> new DataNotFoundException("Member doesn't exist."));
    }

    public Member findMemberById(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(
                () -> new DataNotFoundException("Member doesn't exist."));
    }

    public Member findMemberByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow(
                () -> new DataNotFoundException("Member doesn't exist."));
    }
}
