package spring.bbs.written.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.bbs.member.domain.Member;
import spring.bbs.written.post.domain.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(long postId);
    Page<Post> findAll(Specification<Post> specification, Pageable pageable);
    List<Post> findAllByAuthor(Member author);
}
