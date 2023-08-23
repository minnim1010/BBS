package spring.bbs.post.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostDeleteServiceRequest {
    private Long id;
    private String curMemberName;

    @Builder
    private PostDeleteServiceRequest(Long id, String curMemberName) {
        this.id = id;
        this.curMemberName = curMemberName;
    }

    public static PostDeleteServiceRequest of(Long postId, String curMemberName) {
        return PostDeleteServiceRequest.builder()
            .id(postId)
            .curMemberName(curMemberName)
            .build();
    }
}
