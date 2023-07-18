package spring.bbs.member.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.exceptionhandler.exception.ExistedMemberNameException;
import spring.bbs.exceptionhandler.exception.NotSamePasswordException;
import spring.bbs.member.domain.Member;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.dto.response.JoinResponse;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.util.SecurityUtil;

import static spring.bbs.member.dto.util.MemberToResponse.convertMemberToResponse;
import static spring.bbs.member.dto.util.RequestToMember.convertRequestToMember;

@Service
public class MemberService {

    private final Logger logger = LoggerFactory.getLogger(
            this.getClass());

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
        Member member = convertRequestToMember(req, encodedPassword);
        Member savedMember = memberRepository.save(member);

        logger.debug("Saved member:\n {}", savedMember);

        return convertMemberToResponse(savedMember);
    }

    @Transactional
    public void deleteMember(){
        Member deleteMember = _getMember(_getCurrentLoginedUser());
        memberRepository.delete(deleteMember);
        logger.debug("delete member: {}", deleteMember.getName());
    }

    private boolean isSamePassword(String password, String checkPassword){
        if(password.equals(checkPassword))
            return true;

        logger.debug("{} {}", password, checkPassword);
        throw new NotSamePasswordException("Passwords do not match.");
    }

    private boolean isValidName(String name){
        if(memberRepository.existsByName(name))
            throw new ExistedMemberNameException("Username already exists.");

        return true;
    }

    private Member _getMember(String name) {
        logger.debug("{}", name);
        return memberRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Member doesn't exist."));
    }

    private String _getCurrentLoginedUser(){
        return SecurityUtil.getCurrentUsername().orElseThrow(
                () -> new BadCredentialsException("Can't get current logined user."));
    }
}
