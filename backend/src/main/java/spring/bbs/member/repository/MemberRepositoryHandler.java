package spring.bbs.member.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.bbs.common.exceptionhandling.exception.DataNotFoundException;
import spring.bbs.member.domain.Member;

@RequiredArgsConstructor
@Component
public class MemberRepositoryHandler {

    private final MemberRepository memberRepository;

    public Member findByName(String authorName) {
        return memberRepository.findByName(authorName).orElseThrow(
            () -> new DataNotFoundException(authorName));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(
            () -> new DataNotFoundException(email));
    }
}
