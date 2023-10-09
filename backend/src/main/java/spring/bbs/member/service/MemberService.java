package spring.bbs.member.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.bbs.auth.controller.dto.response.UserInfoResponse;
import spring.bbs.common.exceptionhandling.exception.DataNotFoundException;
import spring.bbs.common.exceptionhandling.exception.DuplicatedMemberNameException;
import spring.bbs.common.exceptionhandling.exception.NotSamePasswordException;
import spring.bbs.member.controller.dto.JoinRequest;
import spring.bbs.member.controller.dto.JoinResponse;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.repository.PostRepository;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public JoinResponse createMember(@NonNull JoinRequest req) {
        validatePassword(req.getPassword(), req.getCheckPassword());
        validateName(req.getName());

        String encodedPassword = passwordEncoder.encode(req.getPassword());
        Member member = Member.of(req, "ROLE_USER", encodedPassword, true);
        Member savedMember = memberRepository.save(member);

        return JoinResponse.from(savedMember);
    }

    private void validatePassword(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new NotSamePasswordException();
        }
    }

    private void validateName(String name) {
        if (memberRepository.existsByName(name)) {
            throw new DuplicatedMemberNameException(name);
        }
    }

    @Transactional
    public void deleteMember(@NonNull String name) {
        Member member = findByName(name);

        postRepository.deleteAllInBatchByAuthor(member);
        memberRepository.delete(member);
    }

    private Member findByName(String authorName) {
        return memberRepository.findByName(authorName)
            .orElseThrow(() -> new DataNotFoundException(authorName));
    }

    public UserInfoResponse get(String name) {
        Member member = memberRepository.findByName(name)
            .orElseThrow(() -> new DataNotFoundException(name));
        return UserInfoResponse.of(member);
    }
}
