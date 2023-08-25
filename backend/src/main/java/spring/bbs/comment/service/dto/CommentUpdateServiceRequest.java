package spring.bbs.comment.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentUpdateServiceRequest {

    private String content;
    private Long commentId;
    private String curMemberName;

    @Builder
    private CommentUpdateServiceRequest(String content, Long commentId, String curMemberName) {
        this.content = content;
        this.commentId = commentId;
        this.curMemberName = curMemberName;
    }
}
