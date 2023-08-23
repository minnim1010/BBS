package spring.bbs.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spring.bbs.member.domain.Member;
import spring.bbs.post.controller.dto.request.PostListRequest;
import spring.bbs.post.controller.dto.request.PostRequest;
import spring.bbs.post.controller.dto.response.PostListResponse;
import spring.bbs.post.controller.dto.response.PostResponse;
import spring.bbs.post.service.PostService;
import spring.bbs.post.service.dto.PostDeleteServiceRequest;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPost(@PathVariable("id") long postId) {
        return postService.getPost(postId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PostListResponse> getPostList(@Valid PostListRequest req) {
        return postService.getPostList(req);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PostResponse createPost(@RequestBody @Valid PostRequest req,
                                   @AuthenticationPrincipal Member currentMember) {
        return postService.createPost(req.toServiceRequest(currentMember.getName()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(@PathVariable("id") long postId,
                           @AuthenticationPrincipal Member currentMember) {
        postService.deletePost(PostDeleteServiceRequest.of(postId, currentMember.getName()));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse modifyPost(@RequestBody @Valid PostRequest req,
                                   @PathVariable("id") long postId,
                                   @AuthenticationPrincipal Member currentMember) {
        return postService.updatePost(req.toServiceRequest(currentMember.getName(), postId));
    }
}
