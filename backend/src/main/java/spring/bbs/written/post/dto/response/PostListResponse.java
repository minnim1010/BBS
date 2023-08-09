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
public class PostListResponse {
    private long id;
    private String title;
    private LocalDateTime createdTime;
    private MemberResponse authorResponse;

    public PostListResponse(long id, String title, LocalDateTime createdTime, MemberResponse memberResponse) {
        this.id = id;
        this.title = title;
        this.createdTime = createdTime;
        this.authorResponse = memberResponse;
    }
}
