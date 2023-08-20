package spring.bbs.comment.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.bbs.comment.domain.Comment;
import spring.bbs.common.exception.DataNotFoundException;

@Component
@RequiredArgsConstructor
public class CommentRepositoryHandler {
    private final CommentRepository commentRepository;

    public Comment findById(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
            () -> new DataNotFoundException("댓글이 존재하지 않습니다."));
    }
}
