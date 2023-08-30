package spring.bbs.post.controller.dto.request;

import lombok.*;

@ToString
@Setter
@Getter
@NoArgsConstructor
public class PostListRequest {
    private int page;
    private String category = "string";
    private String searchScope;
    private String searchKeyword;

    @Builder
    public PostListRequest(int page, String category, String searchScope, String searchKeyword) {
        this.page = page;
        this.category = category;
        this.searchScope = searchScope;
        this.searchKeyword = searchKeyword;
    }
}
