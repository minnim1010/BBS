package spring.bbs.post.dto.request;

public class PostListRequest {
    private int page = 1;
    private String category = "string";
    private String searchScope;
    private String searchKeyword;

    public PostListRequest() {
    }

    public PostListRequest(int page, String category, String scope, String keyword) {
        this.page = page;
        this.category = category;
        this.searchScope = scope;
        this.searchKeyword = keyword;
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
