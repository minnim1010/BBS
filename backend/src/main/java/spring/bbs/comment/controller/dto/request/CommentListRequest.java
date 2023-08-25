package spring.bbs.comment.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import spring.bbs.comment.service.dto.CommentListServiceRequest;

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
        searchKeyword = keyword;
        this.postId = postId;
    }

    public CommentListServiceRequest toServiceRequest() {
        return CommentListServiceRequest.builder()
            .page(page)
            .searchKeyword(searchKeyword)
            .postId(postId)
            .build();
    }
}
