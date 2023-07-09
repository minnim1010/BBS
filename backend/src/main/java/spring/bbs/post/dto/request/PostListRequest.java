package spring.bbs.post.dto.request;

import org.springframework.util.StringUtils;

public class PostListRequest {
    private int page;
    private String category;
    private String searchScope;
    private String searchKeyword;

    public PostListRequest() {
    }

    public PostListRequest(int page, String category, String scope, String keyword) {
        this.page = page;
        this.category = category;
        this.searchScope = scope;
        this.searchKeyword = keyword;

        if(page <= 0)
            page = 1;
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

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getSearchScope() {
        return searchScope;
    }

    public void setSearchScope(String searchScope) {
        this.searchScope = searchScope;
    }

}
