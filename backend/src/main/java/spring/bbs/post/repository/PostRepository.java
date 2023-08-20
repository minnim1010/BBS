package spring.bbs.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.bbs.member.domain.Member;
import spring.bbs.post.domain.Post;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    Optional<Post> findById(long postId);

    void deleteAllInBatchByAuthor(Member author);
}
