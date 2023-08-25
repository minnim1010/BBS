package spring.bbs.comment.repository;

import spring.bbs.comment.domain.Comment;
import spring.bbs.post.domain.Post;

import java.util.List;

public interface CommentCustomRepository {
    List<Comment> findAllByPost(Post post);
}
