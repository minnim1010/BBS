package spring.bbs.comment.repository;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import spring.bbs.comment.domain.Comment;
import spring.bbs.post.domain.Post;

public interface CommentCustomRepository {
    PageImpl<Comment> findAllByPost(Post post, Pageable pageable);
    PageImpl<Comment> findAllByPostAndSearchKeyword(Post post, String searchKeyword, Pageable pageable);

    void updateOrder(Post post, Long groupNum, int updateStartOrder);
    int findLatestOrderWithSameParent(Comment parentComment);
}
