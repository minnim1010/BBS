package spring.bbs.comment.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import spring.bbs.comment.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {
    @Override
    void deleteById(@NonNull Long commentId);
}
