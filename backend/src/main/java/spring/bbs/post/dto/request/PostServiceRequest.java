package spring.bbs.post.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostServiceRequest {
    private String title;
    private String content;
    private String category;
    private String curMemberName;

    @Builder
    private PostServiceRequest(String title, String content, String category, String curMemberName) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.curMemberName = curMemberName;
    }
}
