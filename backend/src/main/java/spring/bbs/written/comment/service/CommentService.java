package spring.bbs.written.comment.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import spring.bbs.member.domain.Member;
import spring.bbs.util.CommonUtil;
import spring.bbs.written.comment.domain.Comment;
import spring.bbs.written.comment.dto.request.CommentCreateRequest;
import spring.bbs.written.comment.dto.request.CommentListRequest;
import spring.bbs.written.comment.dto.request.CommentUpdateRequest;
import spring.bbs.written.comment.dto.response.CommentResponse;
import spring.bbs.written.comment.dto.util.CommentToResponse;
import spring.bbs.written.comment.dto.util.RequestToComment;
import spring.bbs.written.comment.repository.CommentRepository;
import spring.bbs.written.post.domain.Post;

import java.time.LocalDateTime;
import java.util.List;

import static spring.bbs.written.comment.dto.util.CommentToResponse.convertCommentToResponse;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final Logger logger = LoggerFactory.getLogger(
            this.getClass());

    private final int pageSize = 10;

    private final CommentRepository commentRepository;
    private final CommonUtil util;

    public List<CommentResponse> getCommentsByPost(CommentListRequest req){
        logger.debug("CommentService:getCommentsByPost");

        int page = req.getPage();
        if(page <= 0)
            page = 1;
        int offset = (page - 1) * pageSize;
        Post post = util.getPost(req.getPostId());
        Pageable pageable = PageRequest.of(offset, pageSize, Sort.by("createdTime").descending());

        Specification<Comment> spec = getSpecification(post, req.getKeyword());
        List<Comment> comments = commentRepository.findAll(spec, pageable);
        return comments.stream()
                        .map(CommentToResponse::convertCommentToResponse)
                        .toList();
    }

    private Specification<Comment> getSpecification(Post post, String searchKeyword){
        Specification<Comment> specification = Specification.where(null);

        specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("post"), post));
        logger.debug("Specification post: id {}", post.getId());

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

        Member author = util.getMember(util.getCurrentLoginedUser());
        Post post = util.getPost(req.getPostId());
        Comment parentComment = null;
        if(req.getParentCommentId() != 0)
            parentComment = util.getComment(req.getParentCommentId());

        Comment comment = RequestToComment.convertRequestToComment(req.getContent(), author, post, parentComment);
        Comment savedComment = commentRepository.save(comment);

        return convertCommentToResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(CommentUpdateRequest req, long commentId){
        logger.debug("CommentService:updateComment");

        Comment comment = util.getComment(commentId);
        util.validAuthor(comment.getAuthor().getName());

        comment.setContent(req.getContent());
        comment.setModifiedTime(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return convertCommentToResponse(savedComment);
    }

    @Transactional
    public void deleteComment(long commentId){
        logger.debug("CommentService:deleteComment");

        Comment comment = util.getComment(commentId);
        util.validAuthor(comment.getAuthor().getName());

        commentRepository.delete(comment);
    }

}
