package spring.bbs.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.entity.PostList;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(long postId);

    @Query(value = "select "
            + "id, title, createdTime, author "
            + "from Post "
            + "where category = ?1"
            + "order by created_time DESC "
            + "between ?2 and ?3"
            + "Limit ?4", nativeQuery = true)
    List<PostList> findByCategory(String category, int pageStart, int pageEnd, int pageSize);

    @Query(value = "select "
            + "id, title, createdTime, author "
            + "from Post "
            + "order by created_time DESC "
            + "between ?1 and ?2"
            + "Limit ?3", nativeQuery = true)
    List<PostList> findAll(int pageStart, int pageEnd, int pageSize);

    @Query(value = "select "
            + "id, title, createdTime, author "
            + "from Post "
            + "WHERE ?1 LIKE concat('%', ?2, '%')"
            + "order by created_time DESC "
            + "between ?3 and ?4"
            + "Limit ?5", nativeQuery = true)
    List<PostList> findAllByKeyword(String scope, String keyword, int pageStart, int pageEnd, int pageSize);
}
