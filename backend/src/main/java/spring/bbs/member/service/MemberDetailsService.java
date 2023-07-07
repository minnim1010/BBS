package spring.bbs.member.service;

import java.util.List;
import jakarta.transaction.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

@Component("UserDetailsService")
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findWithAuthorityByName(username)
                .map(member -> createUser(username, member))
                .orElseThrow(() -> new UsernameNotFoundException(username + " can't find user."));
    }

    private org.springframework.security.core.userdetails.User createUser(String username, Member member) {
        if (!member.isActivated())
            throw new RuntimeException(username + " is not activated.");

        List<GrantedAuthority> grantedAuthorities =
                List.of(new SimpleGrantedAuthority(member.getAuthority().getRole()));

        return new org.springframework.security.core.userdetails.User(
                member.getName(), member.getPassword(), grantedAuthorities);
    }

}