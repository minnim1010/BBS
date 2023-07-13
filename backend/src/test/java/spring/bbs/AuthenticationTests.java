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
    private void createUser() {
        memberRepository.findByName(memberName)
                .ifPresent(m -> memberRepository.delete(m));

        Member member = new Member();
        member.setName(memberName);
        member.setPassword(passwordEncoder.encode(memberName));
        member.setEmail(memberName + "@test.com");
        member.setActivated(true);
        member.setAuthority(new Authority(memberRole));
        Member savedMember = memberRepository.save(member);

        logger.debug("saved member: {}", savedMember.getName());
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
