package spring.bbs.written.comment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotBlank
    private String content;
    @NotNull
    @Min(1)
    private Long postId;
    private Long parentCommentId;

    @Override
    public String toString() {
        return "CommentCreateRequest{" +
                "content='" + content + '\'' +
                ", postId=" + postId +
                ", parentCommentId=" + parentCommentId +
                '}';
    }
}
