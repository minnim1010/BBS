package spring.bbs.comment.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
