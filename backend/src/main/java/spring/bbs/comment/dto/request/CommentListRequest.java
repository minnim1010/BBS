package spring.bbs.comment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommentListRequest {
    @NotBlank
    private Long postId;
    @Min(1)
    private int page;
    private String searchKeyword;

    public CommentListRequest(int page, String keyword, long postId) {
        this.page = page;
        this.searchKeyword = keyword;
        this.postId = postId;
    }
}
