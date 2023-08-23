package spring.bbs.post.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostListRequest {
    private int page;
    @NotBlank
    private String category = "string";
    private String searchScope;
    private String searchKeyword;

    @Builder
    private PostListRequest(int page, String category, String scope, String keyword) {
        this.page = page;
        this.category = category;
        searchScope = scope;
        searchKeyword = keyword;
    }
}
