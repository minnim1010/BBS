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

    public CommentListRequest(Long postId) {
        this.postId = postId;
    }

    public CommentListServiceRequest toServiceRequest() {
        return CommentListServiceRequest.builder()
            .postId(postId)
            .build();
    }
}
