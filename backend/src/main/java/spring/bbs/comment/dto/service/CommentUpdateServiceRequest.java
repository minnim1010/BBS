package spring.bbs.comment.dto.service;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.bbs.comment.dto.request.CommentUpdateRequest;

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
