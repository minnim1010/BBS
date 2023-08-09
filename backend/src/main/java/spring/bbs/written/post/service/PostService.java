package spring.bbs.written.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.member.domain.Member;
import spring.bbs.util.CommonUtil;
import spring.bbs.written.post.domain.Post;
import spring.bbs.written.post.dto.request.MediaPostRequest;
import spring.bbs.written.post.dto.request.PostListRequest;
import spring.bbs.written.post.dto.request.PostRequest;
import spring.bbs.written.post.dto.response.MediaPostResponse;
import spring.bbs.written.post.dto.response.PostListResponse;
import spring.bbs.written.post.dto.response.PostResponse;
import spring.bbs.written.post.dto.util.PostToResponse;
import spring.bbs.written.post.dto.util.RequestToPost;
import spring.bbs.written.post.repository.PostRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommonUtil findEntity;

    public PostResponse getPost(long postId) {
        log.debug("PostService.getPost");
        return PostToResponse.convertPostToResponse(findEntity.getPost(postId));
    }

    public Page<PostListResponse> getPostList(PostListRequest req) {
        log.debug("PostService.getPostList");

        final int pageSize = 10;
        int page = req.getPage();
        if(page <= 0)
            page = 1;
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by("createdTime").descending());
        Specification<Post> spec = getSpecification(req.getCategory(), req.getSearchScope(), req.getSearchKeyword());
        Page<Post> postList = postRepository.findAll(spec, pageable);
        return postList.map(PostToResponse::convertPostToPostListResponse);
    }

    private Specification<Post> getSpecification(String category, String searchScope, String searchKeyword){
        Specification<Post> specification = Specification.where(null);

        if (category != null && !category.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category"), findEntity.getCategory(category)));
            log.debug("Specification category: {}", category);
        }

        if (searchScope != null && !searchScope.isEmpty() && searchKeyword != null && !searchKeyword.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(searchScope), "%" + searchKeyword + "%"));
            log.debug("Specification searchScope: {}", searchScope);
            log.debug("Specification searchKeyword: {}", searchKeyword);
        }

        return specification;
    }

    @Transactional
    public MediaPostResponse createPost(MediaPostRequest req) {
        log.debug("PostService.createPost");
        log.debug(req.toString());

        String authorName = findEntity.getCurrentLoginedUser();
        Member author = findEntity.getMember(authorName);

        Post post = RequestToPost.convertCreateRequestToPost(req, author, findEntity.getCategory(req.getCategory()));
        Post savedPost = postRepository.save(post);

        return PostToResponse.convertPostToMediaResponse(savedPost);
    }

    @Transactional
    public PostResponse updatePost(PostRequest req, long postId){
        Post post = findEntity.getPost(postId);
        validAuthor(post.getAuthor().getName());

        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setCategory(findEntity.getCategory(req.getCategory()));
        post.setLastModifiedTime(LocalDateTime.now());
        Post savedPost = postRepository.save(post);

        return PostToResponse.convertPostToResponse(savedPost);
    }

    @Transactional
    public void deletePost(long postId){
        Post post = findEntity.getPost(postId);
        validAuthor(post.getAuthor().getName());

        postRepository.delete(post);
    }

    private Post _getPost(long postId){
        return postRepository.findById(postId).orElseThrow(
                () -> new DataNotFoundException("Post doesn't exist."));
    }

    private void validAuthor(String authorName){
        if(!authorName.equals(findEntity.getCurrentLoginedUser()))
            throw new AccessDeniedException("No valid author.");
    }
}
