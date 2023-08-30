package spring.bbs.post.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.bbs.common.exceptionhandling.exception.DataNotFoundException;
import spring.bbs.post.domain.Post;

@RequiredArgsConstructor
@Component
public class PostRepositoryHandler {
    private static final String POST_NOT_FOUND_MSG = "게시글을 찾을 수 없습니다.";

    private final PostRepository postRepository;

    public Post findById(long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
            new DataNotFoundException(POST_NOT_FOUND_MSG));
    }
}
