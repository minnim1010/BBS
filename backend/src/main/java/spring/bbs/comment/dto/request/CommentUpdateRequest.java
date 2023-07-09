package spring.bbs.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CommentUpdateRequest {
    @NotNull
    @NotEmpty
    @NotBlank
    private String content;

    public CommentUpdateRequest() {
    }

    public CommentUpdateRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
