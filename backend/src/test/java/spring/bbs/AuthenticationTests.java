package spring.bbs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.bbs.jwt.JwtProvider;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class AuthenticationTests {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final String AUTHENTICATION_HEADER = "Authorization";

    protected String memberName = "test";
    protected String memberRole = "ROLE_USER";

    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected JwtProvider jwtProvider;

    @PostConstruct
    protected void init() {
        createMember(memberName);
    }

    protected Member createMember(String name){
        Member member = memberRepository.findByName(name)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setName(name);
                    newMember.setPassword(passwordEncoder.encode(name));
                    newMember.setEmail(name + "@test.com");
                    newMember.setActivated(true);
                    newMember.setAuthority(new Authority(memberRole));
                    return memberRepository.save(newMember);
                });

        logger.debug("saved member: {}", member.getName());

        return member;
    }

    protected String getJwtToken(){
        Authentication token =
                new UsernamePasswordAuthenticationToken(
                        memberName, "", List.of(new SimpleGrantedAuthority(memberRole)));
        return jwtProvider.createToken(token);
    }

    protected String getJwtToken(String name){
        Authentication token =
                new UsernamePasswordAuthenticationToken(
                        name, "", List.of(new SimpleGrantedAuthority(memberRole)));
        return jwtProvider.createToken(token);
    }

    protected String getJwtTokenHeader(String token){
        return "Bearer " + token;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }
}
