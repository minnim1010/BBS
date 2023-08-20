package spring.helper;

import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

public class MemberCreator {

    private final MemberRepository memberRepository;

    public MemberCreator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(String name) {
        Member newMember = Member.builder()
            .name(name)
            .password("password")
            .email(name + "@test.com")
            .isEnabled(true)
            .authority(Enum.valueOf(Authority.class, Authority.ROLE_USER.name()))
            .build();
        return memberRepository.save(newMember);
    }

    public Member createMember(String name, String password) {
        Member newMember = Member.builder()
            .name(name)
            .password(password)
            .email(name + "@test.com")
            .isEnabled(true)
            .authority(Enum.valueOf(Authority.class, Authority.ROLE_USER.name()))
            .build();
        return memberRepository.save(newMember);
    }
}
