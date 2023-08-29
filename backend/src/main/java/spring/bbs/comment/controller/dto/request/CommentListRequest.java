package spring.bbs.comment.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import spring.bbs.comment.service.dto.CommentListServiceRequest;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommentListRequest {
    @NotBlank
    private Long postId;
    private Long parentCommentId;

    public CommentListRequest(Long postId, Long parentCommentId) {
        this.postId = postId;
        this.parentCommentId = parentCommentId;
    }

    public CommentListServiceRequest toServiceRequest() {
        return CommentListServiceRequest.builder()
            .postId(postId)
            .parentCommentId(parentCommentId)
            .build();
    }
}
