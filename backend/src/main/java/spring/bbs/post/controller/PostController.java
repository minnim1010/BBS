package spring.bbs.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spring.bbs.common.constant.Api;
import spring.bbs.member.domain.Member;
import spring.bbs.post.controller.dto.request.PostListRequest;
import spring.bbs.post.controller.dto.request.PostRequest;
import spring.bbs.post.controller.dto.response.PostListResponse;
import spring.bbs.post.controller.dto.response.PostResponse;
import spring.bbs.post.service.PostService;
import spring.bbs.post.service.dto.PostDeleteServiceRequest;
import spring.bbs.post.service.dto.PostServiceRequest;
import spring.bbs.post.service.dto.PostUpdateServiceRequest;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(Api.URI_PREFIX + Api.VERSION + Api.Domain.POST)
@RestController
public class PostController {

    private final PostService postService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPost(@PathVariable("id") long postId) {
        return postService.getPost(postId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PostListResponse> getPostList(@ModelAttribute PostListRequest req) {
        return postService.getPostList(req);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PostResponse createPost(@RequestBody @Valid PostRequest req,
                                   @AuthenticationPrincipal Member currentMember) {
        PostServiceRequest serviceRequest = req.toServiceRequest(currentMember.getName());

        return postService.createPost(serviceRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(@PathVariable("id") long postId,
                           @AuthenticationPrincipal Member currentMember) {
        PostDeleteServiceRequest serviceRequest = PostDeleteServiceRequest.of(postId, currentMember.getName());

        postService.deletePost(serviceRequest);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse modifyPost(@PathVariable("id") long postId,
                                   @RequestBody @Valid PostRequest req,
                                   @AuthenticationPrincipal Member currentMember) {
        PostUpdateServiceRequest serviceRequest = req.toServiceRequest(currentMember.getName(), postId);

        return postService.updatePost(serviceRequest);
    }
}

