package spring.bbs.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.bbs.comment.dto.request.CommentCreateRequest;
import spring.bbs.comment.dto.request.CommentListRequest;
import spring.bbs.comment.dto.request.CommentUpdateRequest;
import spring.bbs.comment.dto.response.CommentResponse;
import spring.bbs.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> lookupComments(CommentListRequest req){
        List<CommentResponse> responseList = commentService.getCommentsByPost(req);
        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CommentResponse> writeComment(CommentCreateRequest req){
        CommentResponse response = commentService.createComment(req);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CommentResponse> modifyComment(CommentUpdateRequest req,
                                                         @PathVariable(value="id") long commentId){
        CommentResponse response = commentService.updateComment(req, commentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteComment(@PathVariable(value="id") long commentId){
        commentService.deleteComment(commentId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
