package spring.bbs.post.service;

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
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepositoryHandler memberRepositoryHandler;
    private final PostRepositoryHandler postRepositoryHandler;
    private final CategoryRepositoryHandler categoryRepositoryHandler;

    public PostResponse getPost(long postId) {
        return PostResponse.of(postRepositoryHandler.findById(postId));
    }

    public Page<PostListResponse> getPostList(PostListRequest req) {
        final int pageSize = 10;
        int page = req.getPage();
        if(page <= 0)
            page = 1;
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by("createdTime").descending());
        Specification<Post> spec = getSpecification(req.getCategory(), req.getSearchScope(), req.getSearchKeyword());
        Page<Post> postList = postRepository.findAll(spec, pageable);
        return postList.map(PostListResponse::of);
    }

    private Specification<Post> getSpecification(String category, String searchScope, String searchKeyword){
        Specification<Post> specification = Specification.where(null);

        if (category != null && !category.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category"), categoryRepositoryHandler.findByName(category)));
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
    public PostResponse createPost(PostRequest req) {

        Member author = memberRepositoryHandler.findByName(AuthenticationUtil.getCurrentMemberNameOrAccessDenied());
        Post post = Post.of(req, categoryRepositoryHandler.findByName(req.getCategory()), author);
        Post savedPost = postRepository.save(post);

        return PostResponse.of(savedPost);
    }

    @Transactional
    public PostResponse updatePost(PostRequest req, long postId){
        Post post = postRepositoryHandler.findById(postId);
        String loginedMemberName = AuthenticationUtil.getCurrentMemberNameOrAccessDenied();
        validAuthor(post.getAuthor().getName(), loginedMemberName);

        post.update(req.getTitle(), req.getContent(), categoryRepositoryHandler.findByName(req.getCategory()));
        Post savedPost = postRepository.save(post);

        return PostResponse.of(savedPost);
    }

    @Transactional
    public void deletePost(long postId){
        Post post = postRepositoryHandler.findById(postId);
        validAuthor(post.getAuthor().getName(), AuthenticationUtil.getCurrentMemberNameOrAccessDenied());

        postRepository.delete(post);
    }

    private void validAuthor(String authorName, String loginedMemberName){
        if(!authorName.equals(loginedMemberName))
            throw new AccessDeniedException("작성자여야 합니다.");
    }
}
