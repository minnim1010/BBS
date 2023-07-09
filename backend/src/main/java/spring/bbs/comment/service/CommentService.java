package spring.bbs.comment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.dto.request.CommentCreateRequest;
import spring.bbs.comment.dto.request.CommentListRequest;
import spring.bbs.comment.dto.request.CommentUpdateRequest;
import spring.bbs.comment.dto.response.CommentResponse;
import spring.bbs.comment.repository.CommentRepository;
import spring.bbs.comment.util.CommentToResponse;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepository;
import spring.bbs.util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;

import static spring.bbs.comment.util.CommentToResponse.convertCommentToResponse;
import static spring.bbs.comment.util.RequestToComment.convertRequestToComment;

@Service
public class CommentService {

    private final Logger logger = LoggerFactory.getLogger(
            CommentService.class);

    private final int pageSize = 10;

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository,
                          MemberRepository memberRepository,
                          PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    public List<CommentResponse> getCommentsByPost(CommentListRequest req){
        logger.debug("CommentService:getCommentsByPost");

        int offset = (req.getPage() - 1) * pageSize;
        Pageable pageable = PageRequest.of(offset, pageSize, Sort.by("createdTime").descending());

        Specification<Comment> spec = getSpecification(req.getPostId(), req.getKeyword());
        List<Comment> comments = commentRepository.findAll(spec, pageable);
        return comments.stream()
                        .map(CommentToResponse::convertCommentToResponse)
                        .toList();
    }

    private Specification<Comment> getSpecification(long postId, String searchKeyword){
        Specification<Comment> specification = Specification.where(null);

        specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("postId"), postId));
        logger.debug("Specification postId: {}", postId);

        if(StringUtils.hasText(searchKeyword)){
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("content"), "%" + searchKeyword + "%"));
            logger.debug("Specification searchKeyword: {}", searchKeyword);
        }

        return specification;
    }

    @Transactional
    public CommentResponse createComment(CommentCreateRequest req){
        logger.debug("CommentService:createComment");
        logger.debug(req.toString());

        Member author = _getMember(_getCurrentLoginedUser());
        Post post = _getPost(req.getPostId());
        Comment parentComment = null;
        if(req.getParentCommentId() != 0)
            parentComment = _getComment(req.getParentCommentId());

        Comment comment = convertRequestToComment(req.getContent(), author, post, parentComment);
        Comment savedComment = commentRepository.save(comment);

        return convertCommentToResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(CommentUpdateRequest req, long commentId){
        logger.debug("CommentService:updateComment");

        validAuthor(_getCurrentLoginedUser());

        Comment comment = _getComment(commentId);
        comment.setContent(req.getContent());
        comment.setModifiedTime(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return convertCommentToResponse(savedComment);
    }

    @Transactional
    public void deleteComment(long commentId){
        logger.debug("CommentService:deleteComment");

        validAuthor(_getCurrentLoginedUser());

        Comment comment = _getComment(commentId);
        commentRepository.delete(comment);
    }

    private Post _getPost(long postId){
        return postRepository.findById(postId).orElseThrow(
                () -> new RuntimeException("Post doesn't exist."));
    }

    private Member _getMember(String authorName){
        return memberRepository.findByName(authorName).orElseThrow(
                () -> new RuntimeException("No member exists."));
    }

    private Comment _getComment(long commentId){
        return commentRepository.findById(commentId).orElseThrow(
                () -> new RuntimeException("No comment exists."));
    }

    private String _getCurrentLoginedUser(){
        return SecurityUtil.getCurrentUsername().orElseThrow(
                () -> new RuntimeException("No logined."));
    }

    private void validAuthor(String authorName){
        if(!authorName.equals(_getCurrentLoginedUser()))
            throw new RuntimeException("No valid author.");
    }
}
