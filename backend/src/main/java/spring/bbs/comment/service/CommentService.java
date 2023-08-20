package spring.bbs.comment.service;

import jakarta.persistence.EntityManager;
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
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.dto.response.CommentResponse;
import spring.bbs.comment.dto.service.CommentCreateServiceRequest;
import spring.bbs.comment.dto.service.CommentDeleteServiceRequest;
import spring.bbs.comment.dto.service.CommentListServiceRequest;
import spring.bbs.comment.dto.service.CommentUpdateServiceRequest;
import spring.bbs.comment.repository.CommentRepository;
import spring.bbs.comment.repository.CommentRepositoryHandler;
import spring.bbs.common.exception.DataNotFoundException;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepositoryHandler;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepositoryHandler;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final static int PAGE_SIZE = 20;

    private final CommentRepository commentRepository;
    private final CommentRepositoryHandler commentRepositoryHandler;
    private final PostRepositoryHandler postRepositoryHandler;
    private final MemberRepositoryHandler memberRepositoryHandler;
    private final EntityManager em;

    public Page<CommentResponse> getCommentsByPost(CommentListServiceRequest req) {
        int page = getValidPage(req.getPage());
        Post post = postRepositoryHandler.findById(req.getPostId());
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").descending());
        Page<Comment> comments = findToPageByRequest(req.getSearchKeyword(), post, pageable);

        return comments.map(CommentResponse::of);
    }

    private int getValidPage(int pageInRequest) {
        if (pageInRequest <= 0) {
            return 1;
        }
        return pageInRequest;
    }

    private Page<Comment> findToPageByRequest(String searchKeyword, Post post, Pageable pageable) {
        if (StringUtils.hasText(searchKeyword)) {
            return commentRepository.findAllByPostAndSearchKeyword(post, searchKeyword, pageable);
        }
        return commentRepository.findAllByPost(post, pageable);
    }

    @Transactional
    public CommentResponse createComment(CommentCreateServiceRequest req) {
        Member author = getLoginedMember(req.getCurMemberName());
        Post post = postRepositoryHandler.findById(req.getPostId());
        Comment parentComment = getParentComment(req.getParentCommentId());
        Comment comment = createCommentWithParentComment(req.getContent(), parentComment, post, author);

        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.of(savedComment);
    }

    private Comment createCommentWithParentComment(String content, Comment parentComment, Post post, Member author) {
        if (parentComment != null) {
            int curOrder = getCurOrder(parentComment);
            Long curGroupNum = getCurGroupNum(parentComment);
            commentRepository.updateOrder(post, curGroupNum, curOrder);
            em.flush();
            em.clear();
            return Comment.of(content, author, post, parentComment, curOrder);
        }
        return Comment.of(content, author, post);
    }

    private int getCurOrder(Comment parentComment) {
        int latestOrder = commentRepository.findLatestOrderWithSameParent(parentComment);
        return latestOrder + 1;
    }

    private Long getCurGroupNum(Comment parentComment) {
        Long groupNum = parentComment.getGroupNum();
        if (groupNum == null) {
            groupNum = parentComment.getId();
        }
        return groupNum;
    }

    private Comment getParentComment(Long parentCommentId) {
        if (parentCommentId != null && parentCommentId != 0) {
            return commentRepositoryHandler.findById(parentCommentId);
        }
        return null;
    }

    @Transactional
    public CommentResponse updateComment(CommentUpdateServiceRequest req) {
        Comment comment = findById(req.getCommentId());
        validAuthor(comment.getAuthor().getName(), req.getCurMemberName());

        Comment updatedComment = comment.update(req.getContent());
        return CommentResponse.of(updatedComment);
    }

    private Comment findById(Long commentId) {
        return commentRepositoryHandler.findById(commentId);
    }

    @Transactional
    public void deleteComment(CommentDeleteServiceRequest req) {
        Comment comment = findById(req.getCommentId());
        checkAlreadyDeletedComment(comment);
        validAuthor(comment.getAuthor().getName(), req.getCurMemberName());

        deleteCommentByChildExists(comment);
    }

    private void checkAlreadyDeletedComment(Comment comment) {
        if (comment.isDeleted()) {
            throw new DataNotFoundException("댓글이 존재하지 않습니다.");
        }
    }

    private void deleteCommentByChildExists(Comment comment) {
        if (changeDeletedStatusIfCannotDeleted(comment)) {
            return;
        }

        Comment parentComment = comment.getParentComment();
        if (deleteCommentNotRecomment(comment, parentComment)) {
            return;
        }
        deleteRecomment(comment, parentComment);
    }

    private boolean changeDeletedStatusIfCannotDeleted(Comment comment) {
        if (!comment.isCanDeleted()) {
            comment.delete();
            return true;
        }
        return false;
    }

    private boolean deleteCommentNotRecomment(Comment comment, Comment parentComment) {
        if (parentComment == null) {
            commentRepository.delete(comment);
            return true;
        }
        return false;
    }

    private void deleteRecomment(Comment comment, Comment parentComment) {
        int childNum = commentRepository.countByParentComment(parentComment);
        if (childNum == 1) {
            parentComment.setCanDeleted(true);
            comment.delete();
            return;
        }
        commentRepository.delete(comment);
    }


    private Member getLoginedMember(String memberName) {
        return memberRepositoryHandler.findByName(memberName);
    }

    private void validAuthor(String authorName, String curMemberName) {
        if (!authorName.equals(curMemberName)) {
            throw new AccessDeniedException("작성자여야 합니다.");
        }
    }
}
