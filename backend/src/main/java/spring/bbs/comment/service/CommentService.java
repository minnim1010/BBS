package spring.bbs.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.bbs.comment.controller.dto.response.CommentResponse;
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.repository.CommentRepository;
import spring.bbs.comment.service.dto.CommentCreateServiceRequest;
import spring.bbs.comment.service.dto.CommentDeleteServiceRequest;
import spring.bbs.comment.service.dto.CommentListServiceRequest;
import spring.bbs.comment.service.dto.CommentUpdateServiceRequest;
import spring.bbs.common.exception.DataNotFoundException;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepositoryHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepositoryHandler postRepositoryHandler;
    private final CommentRepository commentRepository;

    public List<CommentResponse> getCommentsByParent(Long parentCommentId) {
        Comment parentComment = commentRepository.findById(parentCommentId)
            .orElseThrow(() -> new DataNotFoundException("답글을 달 댓글이 존재하지 않습니다."));

        List<spring.bbs.comment.repository.dto.CommentResponse> comments = commentRepository.findAllByParent(parentComment);

        return comments.stream()
            .map(CommentResponse::of)
            .collect(Collectors.toList());
    }

    public List<CommentResponse> getCommentsByPost(CommentListServiceRequest req) {
        List<spring.bbs.comment.repository.dto.CommentResponse> comments = commentRepository.findAllByPost(req.getPostId());

        return comments.stream()
            .map(CommentResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse createComment(CommentCreateServiceRequest request) {
        Member author = findByName(request.getCurMemberName());

        Post post = postRepositoryHandler.findById(request.getPostId());

        Long parentCommentId = request.getParentCommentId();
        Comment newComment = createNewCommentEntity(request.getContent(), parentCommentId, author, post);

        Comment savedComment = commentRepository.save(newComment);
        return CommentResponse.of(savedComment);
    }

    private Comment createNewCommentEntity(String content, Long parentCommentId, Member author, Post post) {
        if (parentCommentId != null && parentCommentId > 0) {
            Comment parentComment = findById(parentCommentId);
            return Comment.createReply(content, author, post, parentComment);
        }
        return Comment.create(content, author, post);
    }

    public Member findByName(String authorName) {
        return memberRepository.findByName(authorName).orElseThrow(
            () -> new AccessDeniedException("유효하지 않은 사용자입니다."));
    }

    @Transactional
    public CommentResponse updateComment(CommentUpdateServiceRequest req) {
        Comment comment = findById(req.getCommentId());

        validAuthor(comment.getAuthor().getName(), req.getCurMemberName());

        Comment updatedComment = comment.update(req.getContent());
        return CommentResponse.of(updatedComment);
    }

    private Comment findById(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
            () -> new DataNotFoundException("댓글이 존재하지 않습니다."));
    }

    private void validAuthor(String authorName, String curMemberName) {
        if (!authorName.equals(curMemberName)) {
            throw new AccessDeniedException("작성자여야 합니다.");
        }
    }

    @Transactional
    public void deleteComment(CommentDeleteServiceRequest req) {
        Comment comment = findById(req.getCommentId());

        validAuthor(comment.getAuthor().getName(), req.getCurMemberName());

        commentRepository.delete(comment);
    }
}
