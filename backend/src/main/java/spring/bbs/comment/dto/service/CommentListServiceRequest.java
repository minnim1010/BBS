package spring.bbs.comment.dto.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
public class CommentListServiceRequest {
    private Long postId;
    private int page;
    private String searchKeyword;

    @Builder
    private CommentListServiceRequest(int page, String searchKeyword, long postId) {
        this.page = page;
        this.searchKeyword = searchKeyword;
        this.postId = postId;
    }
}
