package spring.bbs.post.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.bbs.member.controller.dto.MemberResponse;
import spring.bbs.post.domain.Post;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostResponse {
    private long id;
    private String title;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private MemberResponse author;
    private String category;

    @Builder
    private PostResponse(long id,
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
        this.author = author;
        this.category = category;
    }

    public static PostResponse of(Post post) {
        return PostResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .createdTime(post.getCreatedTime())
            .modifiedTime(post.getLastModifiedTime())
            .author(MemberResponse.of(post.getAuthor()))
            .category(post.getCategory().getName())
            .build();
    }
}
