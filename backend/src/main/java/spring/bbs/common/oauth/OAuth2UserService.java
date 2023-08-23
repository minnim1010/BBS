package spring.bbs.common.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);

        return user;
    }

    private Member saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");

        Member member = memberRepository.findByEmail(email)
            .map(m -> m.updateOAuthName(name))
            .orElse(Member.builder()
                .name(name)
                .oauthName(name)
                .email(email)
                .isEnabled(true)
                .authority(Authority.ROLE_USER)
                .build());

        return memberRepository.save(member);
    }
}
