package spring.bbs.post.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
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
