package spring.bbs.written.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostRequest {
    @Size(max = 100)
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotEmpty
    private String category;

    public PostRequest(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
