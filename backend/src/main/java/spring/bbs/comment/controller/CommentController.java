package spring.bbs.comment.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spring.bbs.comment.controller.dto.request.CommentCreateRequest;
import spring.bbs.comment.controller.dto.request.CommentListRequest;
import spring.bbs.comment.controller.dto.request.CommentUpdateRequest;
import spring.bbs.comment.controller.dto.response.CommentResponse;
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.service.CommentService;
import spring.bbs.comment.service.dto.CommentDeleteServiceRequest;
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

    @GetMapping
    public ResponseEntity<List<Comment>> getCommentList(@ModelAttribute CommentListRequest req) {
        List<Comment> responseList = commentService.getCommentsByPost(req.toServiceRequest());
        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody @Valid CommentCreateRequest req,
                                                         @AuthenticationPrincipal Member currentMember) {
        CommentResponse response = commentService.createComment(
            req.toServiceRequest(currentMember.getName()));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommentResponse> modifyComment(@RequestBody @Valid CommentUpdateRequest req,
                                                         @PathVariable("id") Long commentId,
                                                         @AuthenticationPrincipal Member currentMember) {
        CommentResponse response = commentService.updateComment(
            req.toServiceRequest(commentId, currentMember.getName()));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long commentId,
                                              @AuthenticationPrincipal Member currentMember) {
        commentService.deleteComment(
            new CommentDeleteServiceRequest(commentId, currentMember.getName()));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
