package spring.bbs.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import spring.bbs.category.domain.Category;
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.controller.dto.SearchScope;
import spring.bbs.post.controller.dto.request.PostListRequest;
import spring.bbs.post.controller.dto.response.PostListResponse;
import spring.bbs.post.controller.dto.response.PostResponse;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepository;
import spring.bbs.post.repository.PostRepositoryHandler;
import spring.bbs.post.service.dto.PostDeleteServiceRequest;
import spring.bbs.post.service.dto.PostServiceRequest;
import spring.bbs.post.service.dto.PostUpdateServiceRequest;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final static int PAGE_SIZE = 10;

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostRepositoryHandler postRepositoryHandler;
    private final CategoryRepositoryHandler categoryRepositoryHandler;

    public PostResponse getPost(long postId) {
        Post post = postRepositoryHandler.findById(postId);

        return PostResponse.of(post);
    }

    public Page<PostListResponse> getPostList(PostListRequest req) {
        int page = getValidPage(req.getPage());
        
        Page<Post> postList = findPostList(req.getSearchScope(), req.getSearchKeyword(), page);

        return postList.map(PostListResponse::of);
    }

    private int getValidPage(int pageInRequest) {
        if (pageInRequest <= 0) {
            return 1;
        }
        return pageInRequest;
    }

    private Page<Post> findPostList(String scope, String keyword, int page) {
        Pageable pageable =
            PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").descending());

        if (StringUtils.hasText(keyword)) {
            validateSearchScope(scope);
            return postRepository.findAllBySearchKeywordAndScope(scope, keyword, pageable);
        }
        return postRepository.findAll(pageable);
    }

    private void validateSearchScope(String scope) {
        if (!SearchScope.contains(scope)) {
            throw new IllegalStateException("해당 검색 범위를 지원하지 않습니다.");
        }
    }

    @Transactional
    public PostResponse createPost(PostServiceRequest req) {
        Member member = memberRepository
            .findByName(req.getCurMemberName())
            .orElseThrow(() -> new AccessDeniedException("로그인해야 합니다."));

        Post post = Post.of(req, categoryRepositoryHandler.findByName(req.getCategory()), member);

        Post savedPost = postRepository.save(post);

        return PostResponse.of(savedPost);
    }

    @Transactional
    public PostResponse updatePost(PostUpdateServiceRequest req) {
        Post post = postRepositoryHandler.findById(req.getId());

        validAuthor(post.getAuthor().getName(), req.getCurMemberName());

        Category category = categoryRepositoryHandler.findByName(req.getCategory());
        post.update(req.getTitle(), req.getContent(), category);

        return PostResponse.of(post);
    }

    private void validAuthor(String authorName, String currentMemberName) {
        if (!authorName.equals(currentMemberName)) {
            throw new AccessDeniedException("작성자여야 합니다.");
        }
    }

    @Transactional
    public void deletePost(PostDeleteServiceRequest req) {
        Post post = postRepositoryHandler.findById(req.getId());

        validAuthor(post.getAuthor().getName(), req.getCurMemberName());

        postRepository.delete(post);
    }
}
