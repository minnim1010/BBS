package spring.bbs.auth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
            .orElseThrow(() -> new UsernameNotFoundException(username + ": 회원을 찾을 수 없습니다."));
    }

    private User createUser(String username, Member member) {
        if (!member.isEnabled()) {
            throw new IllegalStateException(username + ": 활성화되어있지 않은 회원입니다.");
        }

        List<GrantedAuthority> grantedAuthorities =
            List.of(new SimpleGrantedAuthority(member.getAuthority().toString()));

        return new User(member.getName(), member.getPassword(), grantedAuthorities);
    }

}