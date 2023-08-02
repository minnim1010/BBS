package spring.bbs.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bbs.media.domain.Media;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findAllByPostId(Long postId);
}
