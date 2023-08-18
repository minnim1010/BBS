package spring.bbs.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.bbs.exceptionhandler.exception.ExistedMemberNameException;
import spring.bbs.exceptionhandler.exception.NotSamePasswordException;
import spring.bbs.member.domain.Member;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.dto.response.JoinResponse;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.member.repository.MemberRepositoryHandler;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberRepositoryHandler memberRepositoryHandler;

    @Transactional
    public JoinResponse createMember(JoinRequest req){
        validatePassword(req.getPassword(), req.getCheckPassword());
        validateName(req.getName());

        String encodedPassword = passwordEncoder.encode(req.getPassword());
        Member member = Member.of(req, "ROLE_USER", encodedPassword, true);
        Member savedMember = memberRepository.save(member);

        return JoinResponse.of(savedMember);
    }

    @Transactional
    public void deleteMember(String memberName){
        memberRepository.delete(memberRepositoryHandler.findByName(memberName));
    }

    private void validatePassword(String password, String checkPassword){
        if(!password.equals(checkPassword))
            throw new NotSamePasswordException();
    }

    private void validateName(String name){
        if(memberRepository.existsByName(name))
            throw new ExistedMemberNameException();
    }
}
