package spring.bbs.written.post.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostListRequest {
    private int page = 1;
    private String category = "string";
    private String searchScope;
    @Size(max=20)
    private String searchKeyword;

    public PostListRequest(int page, String category, String scope, String keyword) {
        this.page = page;
        this.category = category;
        this.searchScope = scope;
        this.searchKeyword = keyword;
    }
}
