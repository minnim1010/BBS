package spring.bbs.written.post.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String category;
    private List<MultipartFile> mediaFiles;
}
