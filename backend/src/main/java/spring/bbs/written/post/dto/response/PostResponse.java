package spring.bbs.written.post.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.bbs.member.dto.response.MemberResponse;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class PostResponse {
    private long id;
    private String title;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private MemberResponse authorResponse;
    private String category;

    public PostResponse(long id,
                        String title,
                        String content,
                        LocalDateTime createdTime,
                        LocalDateTime modifiedTime,
                        MemberResponse author,
                        String category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.authorResponse = author;
        this.category = category;
    }
}
