package spring.bbs.post.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaPostRequest {
    @Size(max = 100)
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    @NotEmpty
    private String category;
    @NotEmpty
    private List<MultipartFile> mediaFiles;
}
