package spring.bbs.comment.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentListServiceRequest {
    private Long postId;

    @Builder
    private CommentListServiceRequest(Long postId) {
        this.postId = postId;
    }
}
