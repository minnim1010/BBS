package spring.bbs.post.controller.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@ToString
@Setter
public class PostListRequest {
    private int page;
    private String category = "string";
    private String searchScope;
    private String searchKeyword;

    @Builder
    private PostListRequest(int page, String category, String scope, String keyword) {
        page = page;
        category = category;
        searchScope = scope;
        searchKeyword = keyword;
    }
}
