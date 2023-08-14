package spring.bbs.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import spring.bbs.member.domain.Member;

@Getter
public class MemberResponse {
    private Long id;
    private String name;

    public MemberResponse() {
    }

    @Builder
    private MemberResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static MemberResponse of(Member author) {
        return MemberResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .build();
    }

    public static MemberResponse create(long id, String name){
        return new MemberResponse(id, name);
    }
}
