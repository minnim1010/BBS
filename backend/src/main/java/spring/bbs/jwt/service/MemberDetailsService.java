package spring.bbs.jwt.service;

import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

import java.util.List;

@Component("UserDetailsService")
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByName(username)
                .map(member -> createUser(username, member))
                .orElseThrow(() -> new UsernameNotFoundException(username + " can't find user."));
    }

    private User createUser(String username, Member member) {
        if (!member.isEnabled())
            throw new RuntimeException(username + " is not enabled.");

        List<GrantedAuthority> grantedAuthorities =
                List.of(new SimpleGrantedAuthority(member.getAuthority().toString()));

        return new User(member.getName(), member.getPassword(), grantedAuthorities);
    }

}