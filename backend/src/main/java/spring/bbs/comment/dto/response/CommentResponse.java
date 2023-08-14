package spring.bbs.comment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.bbs.comment.domain.Comment;
import spring.bbs.member.dto.response.MemberResponse;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private long id;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private MemberResponse author;
    private Long parentCommentId;

    @Builder
    private CommentResponse(Long id,
                            String content,
                            LocalDateTime createdTime,
                            LocalDateTime modifiedTime,
                            MemberResponse author,
                            Long parentCommentId) {
        this.id = id;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.author = author;
        this.parentCommentId = parentCommentId;
    }

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .createdTime(comment.getCreatedTime())
            .modifiedTime(comment.getLastModifiedTime())
            .author(MemberResponse.of(comment.getAuthor()))
            .parentCommentId(comment.getParentComment() == null ?
                null : comment.getParentComment().getId())
            .build();
    }
}
