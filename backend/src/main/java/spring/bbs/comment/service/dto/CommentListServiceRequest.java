package spring.bbs.comment.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentListServiceRequest {
    private Long postId;
    private Long parentCommentId;

    @Builder
    private CommentListServiceRequest(Long postId, Long parentCommentId) {
        this.postId = postId;
        this.parentCommentId = parentCommentId;
    }
}
