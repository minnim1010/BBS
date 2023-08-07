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
import spring.bbs.util.AuthenticationUtil;

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
        validatePassword(req.getPassword(), req.getCheckPassword());
        validateName(req.getName());

        String encodedPassword = passwordEncoder.encode(req.getPassword());
        Member member = convertRequestToMember(req, "ROLE_USER", encodedPassword);
        Member savedMember = memberRepository.save(member);

        logger.debug("Saved member:\n {}", savedMember);

        return convertMemberToResponse(savedMember);
    }

    @Transactional
    public void deleteMember(){
        Member deleteMember = getCurrentLoginedMember();
        memberRepository.delete(deleteMember);
        logger.debug("delete member: {}", deleteMember.getName());
    }

    private void validatePassword(String password, String checkPassword){
        if(!password.equals(checkPassword))
            throw new NotSamePasswordException("Passwords do not match.");
    }

    private void validateName(String name){
        if(memberRepository.existsByName(name))
            throw new ExistedMemberNameException("Username already exists.");
    }

    private Member getCurrentLoginedMember(){
        return _getMember(_getCurrentLoginedUser());
    }

    private Member _getMember(String name) {
        logger.debug("{}", name);
        return memberRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Member doesn't exist."));
    }

    private String _getCurrentLoginedUser(){
        return AuthenticationUtil.getCurrentUsername().orElseThrow(
                () -> new BadCredentialsException("Can't get current logined user."));
    }
}
