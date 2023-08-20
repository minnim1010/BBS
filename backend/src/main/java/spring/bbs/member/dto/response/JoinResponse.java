package spring.bbs.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import spring.bbs.member.domain.Member;

@Getter
public class JoinResponse {
    private final Long id;
    private final String name;
    private final String email;

    @Builder
    private JoinResponse(
        Long id,
        String name,
        String email
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static JoinResponse create(
        Long id,
        String name,
        String email
    ) {
        return new JoinResponse(id, name, email);
    }

    public static JoinResponse of(
        Member savedMember
    ) {
        return JoinResponse.builder()
            .id(savedMember.getId())
            .name(savedMember.getName())
            .email(savedMember.getEmail())
            .build();
    }
}
