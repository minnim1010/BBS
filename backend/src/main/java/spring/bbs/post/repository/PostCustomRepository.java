package spring.bbs.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spring.bbs.post.domain.Post;

public interface PostCustomRepository {
    Page<Post> findAllToPage(Pageable pageable);
    Page<Post> findAllToPageAndSearchKeywordAndScope(String scope, String keyword, Pageable pageable);
}
