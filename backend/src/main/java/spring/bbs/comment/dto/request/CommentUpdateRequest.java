package spring.bbs.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.bbs.comment.dto.service.CommentUpdateServiceRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {

    @NotBlank
    private String content;

    public CommentUpdateServiceRequest toServiceRequest(
        Long commentId,
        String curMemberName) {
        return CommentUpdateServiceRequest.builder()
            .content(this.content)
            .commentId(commentId)
            .curMemberName(curMemberName)
            .build();
    }
}
