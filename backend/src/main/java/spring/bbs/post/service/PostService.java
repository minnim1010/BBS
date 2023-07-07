package spring.bbs.post.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Category;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.entity.PostList;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.dto.response.PostResponse;
import spring.bbs.post.repository.PostRepository;
import spring.bbs.util.SecurityUtil;

import java.util.List;

import static spring.bbs.post.dto.util.PostToResponse.convertPostToResponse;
import static spring.bbs.post.dto.util.RequestToPost.convertRequestToPost;

@Service
public class PostService {

    private final Logger logger = LoggerFactory.getLogger(
            PostService.class);

    private final int pageSize = 10;

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostService(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public PostResponse createPost(PostRequest req){
        logger.debug("PostService.createPost");

        String authorName = SecurityUtil.getCurrentUsername().orElseThrow(
                () -> new RuntimeException("No logined"));
        logger.debug(authorName);

        Member author = memberRepository.findByName(authorName).orElseThrow(
                () -> new RuntimeException("No member exists"));
        Post post = convertRequestToPost(
                req.getTitle(), req.getContent(), author, new Category(req.getCategory()));
        Post savedPost = postRepository.save(post);

        return convertPostToResponse(savedPost);
    }

    public PostResponse getPost(long postId){
        logger.debug("PostService.getPost");
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new RuntimeException("Post doesn't exist."));
        return convertPostToResponse(post);
    }

    public List<PostList> getPostList(String category, int page, String scope, String keyword){
        logger.debug("PostService.getPostList");
        int pageStart = (page-1)*pageSize;
        int pageEnd = pageStart+pageSize;

        if(keyword == null){
            if(category == null)
                return postRepository.findAll(pageStart, pageEnd, pageSize);
            return postRepository.findByCategory(category, pageStart, pageEnd, pageSize);
        }

        return postRepository.findAllByKeyword(scope, keyword, pageStart, pageEnd, pageSize);
    }

}
