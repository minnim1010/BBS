package spring.bbs.comment.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spring.bbs.comment.dto.request.CommentCreateRequest;
import spring.bbs.comment.dto.request.CommentListRequest;
import spring.bbs.comment.dto.request.CommentUpdateRequest;
import spring.bbs.comment.dto.response.CommentResponse;
import spring.bbs.comment.dto.service.CommentDeleteServiceRequest;
import spring.bbs.comment.service.CommentService;
import spring.bbs.member.domain.Member;

@RestController
@RequestMapping("/api/v1/comments")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getCommentList(@ModelAttribute CommentListRequest req) {
        log.debug("{}", req.toString());
        Page<CommentResponse> responseList = commentService.getCommentsByPost(req.toServiceRequest());
        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CommentResponse> createComment(@RequestBody @Valid CommentCreateRequest req,
                                                         @AuthenticationPrincipal Member currentMember) {
        CommentResponse response = commentService.createComment(
            req.toServiceRequest(currentMember.getName()));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CommentResponse> modifyComment(@RequestBody @Valid CommentUpdateRequest req,
                                                         @PathVariable("id") Long commentId,
                                                         @AuthenticationPrincipal Member currentMember) {
        CommentResponse response = commentService.updateComment(
            req.toServiceRequest(commentId, currentMember.getName()));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long commentId,
                                              @AuthenticationPrincipal Member currentMember) {
        commentService.deleteComment(
            new CommentDeleteServiceRequest(commentId, currentMember.getName()));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
