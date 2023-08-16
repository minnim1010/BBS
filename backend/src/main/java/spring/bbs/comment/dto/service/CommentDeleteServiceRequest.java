package spring.bbs.comment.dto.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentDeleteServiceRequest {
    private Long commentId;
    private String curMemberName;

    @Builder
    private CommentDeleteServiceRequest(Long commentId, String curMemberName) {
        this.commentId = commentId;
        this.curMemberName = curMemberName;
    }

    public static CommentDeleteServiceRequest of(Long commentId, String curMemberName) {
        return new CommentDeleteServiceRequest(commentId, curMemberName);
    }
}
