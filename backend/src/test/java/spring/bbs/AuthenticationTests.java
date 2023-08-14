package spring.bbs;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
import spring.bbs.util.RoleType;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public abstract class AuthenticationTests implements ProfileConfiguration {
    protected final String AUTHENTICATION_HEADER = "Authorization";
    protected final String TOKEN_PREFIX = "Bearer ";

    protected String memberName = "test";

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
        return memberRepository.findByName(name)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .name(name)
                            .password(passwordEncoder.encode(name))
                            .email(name + "@test.com")
                            .isEnabled(true)
                            .authority(Enum.valueOf(Authority.class, RoleType.user))
                            .build();
                    return memberRepository.save(newMember);
                });
    }

    protected String getJwtToken(){
        Authentication token =
                new UsernamePasswordAuthenticationToken(
                        memberName, "", List.of(new SimpleGrantedAuthority(RoleType.user)));
        return jwtProvider.generateAccessToken(token);
    }

    protected String getJwtToken(String name){
        Authentication token =
                new UsernamePasswordAuthenticationToken(
                        name, "", List.of(new SimpleGrantedAuthority(RoleType.user)));
        return jwtProvider.generateAccessToken(token);
    }

    protected String getJwtTokenHeader(String token){
        return TOKEN_PREFIX + token;
    }

    protected void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
