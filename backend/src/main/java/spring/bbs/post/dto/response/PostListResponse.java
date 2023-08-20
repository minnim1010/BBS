package spring.bbs.post.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.bbs.member.controller.dto.MemberResponse;
import spring.bbs.post.domain.Post;

import java.time.LocalDateTime;

@Getter

@NoArgsConstructor
public class PostListResponse {
    private long id;
    private String title;
    private LocalDateTime createdTime;
    private MemberResponse author;

    @Builder
    private PostListResponse(long id, String title, LocalDateTime createdTime, MemberResponse author) {
        this.id = id;
        this.title = title;
        this.createdTime = createdTime;
        this.author = author;
    }

    public static PostListResponse of(Post post) {
        return PostListResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .createdTime(post.getCreatedTime())
            .author(MemberResponse.of(post.getAuthor()))
            .build();
    }


    public static PostListResponse create(Long id, String title, LocalDateTime createdTime, MemberResponse author) {
        return PostListResponse.builder()
            .id(id)
            .title(title)
            .createdTime(createdTime)
            .author(author)
            .build();
    }
}
