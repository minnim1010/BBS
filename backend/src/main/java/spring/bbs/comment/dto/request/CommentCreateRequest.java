package spring.bbs.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CommentCreateRequest {
    @NotNull
    @NotEmpty
    @NotBlank
    private String content;
    @NotNull
    private int postId;
    private int parentCommentId;

    public CommentCreateRequest() {
    }

    public CommentCreateRequest(String content, int postId, int parentCommentId) {
        this.content = content;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(int parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    @Override
    public String toString() {
        return "CommentCreateRequest{" +
                "content='" + content + '\'' +
                ", postId=" + postId +
                ", parentCommentId=" + parentCommentId +
                '}';
    }
}
