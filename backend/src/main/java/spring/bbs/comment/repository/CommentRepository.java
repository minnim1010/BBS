package spring.bbs.comment.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.repository.dto.CommentResponse;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    @Override
    @Query("SELECT NEW spring.bbs.comment.repository.dto.CommentResponse(" +
        "c.id, c.content, c.createdTime, c.lastModifiedTime, c.parent.id, a.id, a.name) " +
        "FROM Comment c " +
        "JOIN c.author a " +
        "WHERE c.post.id = :postId AND c.parent IS NULL")
    List<CommentResponse> findAllByPost(@Param("postId") Long postId);

    @Override
    @Query("SELECT NEW spring.bbs.comment.repository.dto.CommentResponse(" +
        "c.id, c.content, c.createdTime, c.lastModifiedTime, c.parent.id, a.id, a.name) " +
        "FROM Comment AS c " +
        "JOIN c.author AS a " +
        "WHERE c.parent = :parent")
    List<CommentResponse> findAllByParent(@Param("parent") Comment parent);
}
