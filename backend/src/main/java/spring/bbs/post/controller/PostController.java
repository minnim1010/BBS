package spring.bbs.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.request.PostListRequest;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.dto.response.PostResponse;
import spring.bbs.post.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> readPost(@PathVariable("id") long postId){
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Post>> lookupPostList(@ModelAttribute PostListRequest req){
        List<Post> response = postService.getPostList(
                req.getCategory(), req.getPage(), req.getSearchScope(), req.getSearchKeyword());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Secured("ROLE_USER")
    public ResponseEntity<PostResponse> writePost(PostRequest req){
        PostResponse response = postService.createPost(req);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> deletePost(@PathVariable("id") long postId){
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<PostResponse> modifyPost(PostRequest req,
                                                   @PathVariable("id") long postId){
        PostResponse response = postService.updatePost(req, postId);
        return ResponseEntity.ok(response);
    }
}
