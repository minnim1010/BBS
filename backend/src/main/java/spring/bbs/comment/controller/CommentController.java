package spring.bbs.comment.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.bbs.comment.dto.request.CommentCreateRequest;
import spring.bbs.comment.dto.request.CommentListRequest;
import spring.bbs.comment.dto.request.CommentUpdateRequest;
import spring.bbs.comment.dto.response.CommentResponse;
import spring.bbs.comment.dto.service.CommentDeleteServiceRequest;
import spring.bbs.comment.service.CommentService;
import spring.bbs.util.AuthenticationUtil;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getCommentList(CommentListRequest req) {
        Page<CommentResponse> responseList = commentService.getCommentsByPost(req);
        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CommentResponse> createComment(@RequestBody @Valid CommentCreateRequest req) {
        CommentResponse response = commentService.createComment(req);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CommentResponse> modifyComment(@RequestBody @Valid CommentUpdateRequest req,
            @PathVariable("id") long commentId) {
        CommentResponse response = commentService.updateComment(req, commentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") long commentId) {
        commentService.deleteComment(
            CommentDeleteServiceRequest.of(commentId,
                AuthenticationUtil.getCurrentMemberNameOrAccessDenied()));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
