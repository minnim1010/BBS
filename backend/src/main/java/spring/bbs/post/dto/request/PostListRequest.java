package spring.bbs.post.dto.request;

import org.springframework.util.StringUtils;

public class PostListRequest {
    private int page;
    private String category;
    private String scope;
    private String keyword;

    public PostListRequest() {
    }

    public PostListRequest(int page, String category, String scope, String keyword) {
        this.page = page;
        this.category = category;
        this.scope = scope;
        this.keyword = keyword;

        if(!StringUtils.hasText(scope))
            scope = "title";
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}
