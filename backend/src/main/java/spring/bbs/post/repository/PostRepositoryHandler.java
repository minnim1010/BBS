package spring.bbs.post.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.bbs.common.exceptionhandling.exception.DataNotFoundException;
import spring.bbs.post.domain.Post;

@RequiredArgsConstructor
@Component
public class PostRepositoryHandler {

    private final PostRepository postRepository;

    public Post findById(long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
            new DataNotFoundException(String.valueOf(postId)));
    }
}
