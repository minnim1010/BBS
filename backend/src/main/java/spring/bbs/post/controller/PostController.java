package spring.bbs.post.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import spring.bbs.post.dto.entity.PostList;
import spring.bbs.post.dto.request.PostListRequest;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.dto.response.PostResponse;
import spring.bbs.post.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final Logger logger = LoggerFactory.getLogger(
            PostController.class);

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
    public ResponseEntity<List<PostList>> LookupPostList(@ModelAttribute PostListRequest req){
        List<PostList> response = postService.getPostList(
                req.getCategory(), req.getPage(), req.getScope(), req.getKeyword());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Secured("ROLE_USER")
    public ResponseEntity<PostResponse> writePost(PostRequest req){
        PostResponse response = postService.createPost(req);
        return ResponseEntity.ok(response);
    }
}
