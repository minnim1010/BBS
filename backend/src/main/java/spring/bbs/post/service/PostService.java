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
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepositoryHandler;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.request.PostListRequest;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.dto.response.PostListResponse;
import spring.bbs.post.dto.response.PostResponse;
import spring.bbs.post.repository.PostRepository;
import spring.bbs.post.repository.PostRepositoryHandler;
import spring.bbs.util.AuthenticationUtil;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final static int PAGE_SIZE = 10;

    private final PostRepository postRepository;
    private final MemberRepositoryHandler memberRepositoryHandler;
    private final PostRepositoryHandler postRepositoryHandler;
    private final CategoryRepositoryHandler categoryRepositoryHandler;

    public PostResponse getPost(long postId) {
        return PostResponse.of(postRepositoryHandler.findById(postId));
    }

    public Page<PostListResponse> getPostList(PostListRequest req) {
        int page = getValidPage(req.getPage());
        Page<Post> postList = findBySearchScopeAndKeyword(req.getSearchScope(), req.getSearchKeyword(), page);
        return postList.map(PostListResponse::of);
    }

    private int getValidPage(int pageInRequest) {
        if (pageInRequest <= 0)
            return 1;
        return pageInRequest;
    }

    private Page<Post> findBySearchScopeAndKeyword(String scope, String keyword, int page) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").descending());
        if (StringUtils.hasText(keyword)) {
            validateSearchScope(scope);
            return postRepository.findAllToPageAndSearchKeywordAndScope(
                scope, keyword, pageable);
        }
        return postRepository.findAllToPage(pageable);
    }

    private void validateSearchScope(String scope) {
        if (!(scope.equals("전체") || scope.equals("제목") || scope.equals("작성자"))) {
            throw new IllegalStateException("해당 검색 범위를 지원하지 않습니다.");
        }
    }

    @Transactional
    public PostResponse createPost(PostRequest req) {

        Member author = memberRepositoryHandler.findByName(AuthenticationUtil.getCurrentMemberNameOrAccessDenied());
        Post post = Post.of(req, categoryRepositoryHandler.findByName(req.getCategory()), author);
        Post savedPost = postRepository.save(post);

        return PostResponse.of(savedPost);
    }

    @Transactional
    public PostResponse updatePost(PostRequest req, long postId) {
        Post post = postRepositoryHandler.findById(postId);
        String loginedMemberName = AuthenticationUtil.getCurrentMemberNameOrAccessDenied();
        validAuthor(post.getAuthor().getName(), loginedMemberName);

        post.update(req.getTitle(), req.getContent(), categoryRepositoryHandler.findByName(req.getCategory()));

        return PostResponse.of(post);
    }

    @Transactional
    public void deletePost(long postId) {
        Post post = postRepositoryHandler.findById(postId);
        validAuthor(post.getAuthor().getName(), AuthenticationUtil.getCurrentMemberNameOrAccessDenied());

        postRepository.delete(post);
    }

    private void validAuthor(String authorName, String currentMember) {
        if (!authorName.equals(currentMember))
            throw new AccessDeniedException("작성자여야 합니다.");
    }
}
