package spring.bbs.member.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.bbs.member.domain.Member;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.dto.response.JoinResponse;
import spring.bbs.member.repository.MemberRepository;

import static spring.bbs.member.dto.util.MemberToResponse.MemberToJoinResponse;
import static spring.bbs.member.dto.util.RequestToMember.JoinRequestToMember;

@Service
public class MemberService {

    private final Logger logger = LoggerFactory.getLogger(
            MemberService.class);

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public MemberService(PasswordEncoder passwordEncoder, MemberRepository memberRepository) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public JoinResponse createMember(JoinRequest req){
        isSamePassword(req.getPassword(), req.getCheckPassword());
        isValidName(req.getName());

        String encodedPassword = passwordEncoder.encode(req.getPassword());
        Member member = JoinRequestToMember(req, encodedPassword);
        Member savedMember = memberRepository.save(member);

        logger.debug("Saved member:\n {}", savedMember);

        return MemberToJoinResponse(savedMember);
    }

    private boolean isSamePassword(String password, String checkPassword){
        if(password.equals(checkPassword))
            return true;

        logger.debug("{} {}", password, checkPassword);
        throw new RuntimeException("Passwords do not match.");
    }

    private boolean isValidName(String name){
        if(memberRepository.existsByName(name))
            throw new RuntimeException("Username already exists.");

        return true;
    }

    @Transactional
    public void deleteMember(String name){
        Member deleteMember = getMember(name);
        memberRepository.delete(deleteMember);
    }

    private Member getMember(String name) {
        return memberRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Member doesn't exist."));
    }
}
