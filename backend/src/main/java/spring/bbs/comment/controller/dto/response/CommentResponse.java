package spring.bbs.comment.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.bbs.comment.domain.Comment;
import spring.bbs.member.controller.dto.MemberResponse;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private Long id;
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
            .parentCommentId(comment.getParent() == null ?
                null : comment.getParent().getId())
            .build();
    }

    public static CommentResponse of(spring.bbs.comment.repository.dto.CommentResponse commentResponse) {
        return CommentResponse.builder()
            .id(commentResponse.id())
            .content(commentResponse.content())
            .createdTime(commentResponse.createdTime())
            .modifiedTime(commentResponse.lastModifiedTime())
            .author(new MemberResponse(commentResponse.authorId(), commentResponse.authorName()))
            .parentCommentId(commentResponse.parentCommentId())
            .build();
    }
}
