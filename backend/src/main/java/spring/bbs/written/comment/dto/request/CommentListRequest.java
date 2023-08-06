package spring.bbs.written.comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CommentListRequest {
    @NotNull
    @NotEmpty
    private Long postId;
    private int page = 1;
    private String searchKeyword;

    public CommentListRequest() {
    }

    public CommentListRequest(int page, String keyword, long postId) {
        this.page = page;
        this.searchKeyword = keyword;
        this.postId = postId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getKeyword() {
        return searchKeyword;
    }

    public void setKeyword(String keyword) {
        this.searchKeyword = keyword;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

}
