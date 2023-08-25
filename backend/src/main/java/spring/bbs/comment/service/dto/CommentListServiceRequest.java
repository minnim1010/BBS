package spring.bbs.comment.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
