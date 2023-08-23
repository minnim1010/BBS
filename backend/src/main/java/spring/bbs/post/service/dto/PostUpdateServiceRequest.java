package spring.bbs.post.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostUpdateServiceRequest {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String curMemberName;

    @Builder
    private PostUpdateServiceRequest(Long id, String title, String content, String category, String curMemberName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.curMemberName = curMemberName;
    }
}
