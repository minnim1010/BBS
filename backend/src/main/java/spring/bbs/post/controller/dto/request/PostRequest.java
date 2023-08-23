package spring.bbs.post.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.bbs.post.service.dto.PostServiceRequest;
import spring.bbs.post.service.dto.PostUpdateServiceRequest;

@Getter
@Setter
@NoArgsConstructor
public class PostRequest {
    @Size(max = 100)
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String category;

    @Builder
    public PostRequest(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public PostServiceRequest toServiceRequest(String curMemberName) {
        return PostServiceRequest.builder()
            .title(title)
            .content(content)
            .category(category)
            .curMemberName(curMemberName)
            .build();
    }

    public PostUpdateServiceRequest toServiceRequest(String curMemberName, Long postId) {
        return PostUpdateServiceRequest.builder()
            .id(postId)
            .title(title)
            .content(content)
            .category(category)
            .curMemberName(curMemberName)
            .build();
    }
}
