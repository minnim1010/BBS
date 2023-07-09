package spring.bbs.comment.dto.request;

import jakarta.validation.constraints.NotNull;

public class CommentListRequest {
    int page;
    String searchKeyword;
    @NotNull
    Long postId;

    public CommentListRequest() {
    }

    public CommentListRequest(int page, String keyword, long postId) {
        this.page = page;
        this.searchKeyword = keyword;
        this.postId = postId;

        if(page <= 0)
            page = 1;
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
