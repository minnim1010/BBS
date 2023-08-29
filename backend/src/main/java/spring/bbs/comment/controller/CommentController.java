package spring.bbs.comment.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spring.bbs.comment.controller.dto.request.CommentCreateRequest;
import spring.bbs.comment.controller.dto.request.CommentListRequest;
import spring.bbs.comment.controller.dto.request.CommentUpdateRequest;
import spring.bbs.comment.controller.dto.response.CommentResponse;
import spring.bbs.comment.service.CommentService;
import spring.bbs.comment.service.dto.CommentCreateServiceRequest;
import spring.bbs.comment.service.dto.CommentDeleteServiceRequest;
import spring.bbs.comment.service.dto.CommentListServiceRequest;
import spring.bbs.comment.service.dto.CommentUpdateServiceRequest;
import spring.bbs.member.domain.Member;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/replies")
    public List<CommentResponse> getCommentsByParentComment(@PathVariable("id") Long parentCommentId) {
        return commentService.getCommentsByParent(parentCommentId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CommentResponse> getCommentsByPost(@ModelAttribute CommentListRequest req) {
        CommentListServiceRequest serviceRequest = req.toServiceRequest();

        return commentService.getCommentsByPost(serviceRequest);
    }
    
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public CommentResponse createComment(@RequestBody @Valid CommentCreateRequest req,
                                         @AuthenticationPrincipal Member currentMember) {
        CommentCreateServiceRequest serviceRequest = req.toServiceRequest(currentMember.getName());

        return commentService.createComment(serviceRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    public CommentResponse modifyComment(@RequestBody @Valid CommentUpdateRequest req,
                                         @PathVariable("id") Long commentId,
                                         @AuthenticationPrincipal Member currentMember) {
        CommentUpdateServiceRequest serviceRequest = req.toServiceRequest(commentId, currentMember.getName());

        return commentService.updateComment(serviceRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable("id") Long commentId,
                              @AuthenticationPrincipal Member currentMember) {
        CommentDeleteServiceRequest serviceRequest = new CommentDeleteServiceRequest(commentId, currentMember.getName());

        commentService.deleteComment(serviceRequest);
    }
}
