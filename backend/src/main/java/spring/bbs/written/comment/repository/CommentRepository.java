package spring.bbs.written.comment.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.bbs.member.domain.Member;
import spring.bbs.written.comment.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAll(Specification<Comment> specification, Pageable pageable);
    List<Comment> findAllByAuthor(Member author);
}
