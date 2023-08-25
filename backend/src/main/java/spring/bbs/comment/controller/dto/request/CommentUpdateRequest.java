package spring.bbs.comment.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.bbs.comment.service.dto.CommentUpdateServiceRequest;

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
            .content(content)
            .commentId(commentId)
            .curMemberName(curMemberName)
            .build();
    }
}
