package spring.bbs.comment.dto.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.bbs.comment.dto.request.CommentCreateRequest;

@Getter
@NoArgsConstructor
public class CommentCreateServiceRequest {

    private String content;
    private Long postId;
    private Long parentCommentId;
    private String curMemberName;

    @Builder
    private CommentCreateServiceRequest(String content, Long postId, Long parentCommentId, String curMemberName) {
        this.content = content;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.curMemberName = curMemberName;
    }
}
