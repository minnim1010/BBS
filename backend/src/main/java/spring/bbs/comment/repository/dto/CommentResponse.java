package spring.bbs.comment.repository.dto;

import java.time.LocalDateTime;

public record CommentResponse(Long id,
                              String content,
                              LocalDateTime createdTime,
                              LocalDateTime lastModifiedTime,
                              Long parentCommentId,
                              Long authorId,
                              String authorName) {
}
