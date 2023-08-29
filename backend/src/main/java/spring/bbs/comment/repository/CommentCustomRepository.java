package spring.bbs.comment.repository;

import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.repository.dto.CommentResponse;

import java.util.List;

public interface CommentCustomRepository {
    List<CommentResponse> findAllByPost(Long postId);

    List<CommentResponse> findAllByParent(Comment parent);
}
