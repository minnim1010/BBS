package spring.bbs.post.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Category;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.request.PostListRequest;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.dto.response.PostListResponse;
import spring.bbs.post.dto.response.PostResponse;
import spring.bbs.post.dto.util.PostToResponse;
import spring.bbs.post.repository.PostRepository;
import spring.bbs.util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;

import static spring.bbs.post.dto.util.PostToResponse.convertPostToResponse;
import static spring.bbs.post.dto.util.RequestToPost.convertRequestToPost;

@Service
public class PostService {

    private final Logger logger = LoggerFactory.getLogger(
            PostService.class);

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostService(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    public PostResponse getPost(long postId) {
        logger.debug("PostService.getPost");
        return convertPostToResponse(_getPost(postId));
    }

    public List<PostListResponse> getPostList(PostListRequest req) {
        logger.debug("PostService.getPostList");

        final int pageSize = 10;
        int offset = (req.getPage() - 1) * pageSize;
        Pageable pageable = PageRequest.of(offset, pageSize, Sort.by("createdTime").descending());
        Specification<Post> spec = getSpecification(req.getCategory(), req.getSearchScope(), req.getSearchKeyword());
        List<Post> postList = postRepository.findAll(spec, pageable);

        return postList.stream()
                .map(PostToResponse::convertPostToPostListResponse)
                .toList();
    }

    private Specification<Post> getSpecification(String category, String searchScope, String searchKeyword){
        Specification<Post> specification = Specification.where(null);

        if (category != null && !category.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category"), new Category(category)));
            logger.debug("Specification category: {}", category);
        }

        if (searchScope != null && !searchScope.isEmpty() && searchKeyword != null && !searchKeyword.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(searchScope), "%" + searchKeyword + "%"));
            logger.debug("Specification searchScope: {}", searchScope);
            logger.debug("Specification searchKeyword: {}", searchKeyword);
        }

        return specification;
    }

    @Transactional
    public PostResponse createPost(PostRequest req) {
        logger.debug("PostService.createPost");

        String authorName = _getCurrentLoginedUser();
        Member author = _getMember(authorName);

        Post post = convertRequestToPost(req.getTitle(),
                req.getContent(),
                LocalDateTime.now(),
                null,
                author,
                new Category(req.getCategory()));
        Post savedPost = postRepository.save(post);

        return convertPostToResponse(savedPost);
    }

    @Transactional
    public PostResponse updatePost(PostRequest req, long postId){
        Post post = _getPost(postId);
        validAuthor(post.getAuthor().getName());

        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setCategory(new Category(req.getCategory()));
        post.setModifiedTime(LocalDateTime.now());
        Post savedPost = postRepository.save(post);

        return convertPostToResponse(savedPost);
    }

    @Transactional
    public void deletePost(long postId){
        Post post = _getPost(postId);
        validAuthor(post.getAuthor().getName());

        postRepository.delete(post);
    }

    private Post _getPost(long postId){
        return postRepository.findById(postId).orElseThrow(
                () -> new DataNotFoundException("Post doesn't exist."));
    }

    private Member _getMember(String authorName){
        return memberRepository.findByName(authorName).orElseThrow(
                () -> new DataNotFoundException("Member doesn't exist."));
    }

    private String _getCurrentLoginedUser(){
        return SecurityUtil.getCurrentUsername().orElseThrow(
                () -> new RuntimeException("Can't get current logined user."));
    }

    private void validAuthor(String authorName){
        if(!authorName.equals(_getCurrentLoginedUser()))
            throw new RuntimeException("No valid author.");
    }
}
