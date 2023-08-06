package spring.bbs.written.post.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.bbs.media.dto.response.MediaResponse;
import spring.bbs.member.dto.response.MemberResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MediaPostResponse {
    private long id;
    private String title;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private MemberResponse authorResponse;
    private String category;
    private List<MediaResponse> files;

    public MediaPostResponse(long id, String title, String content, LocalDateTime createdTime, LocalDateTime modifiedTime, MemberResponse authorResponse, String category, List<MediaResponse> files) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.authorResponse = authorResponse;
        this.category = category;
        this.files = files;
    }
}
