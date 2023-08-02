package spring.bbs.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.bbs.media.dto.response.MediaResponse;
import spring.bbs.media.service.MediaService;
import spring.bbs.post.dto.request.MediaPostRequest;
import spring.bbs.post.dto.request.PostListRequest;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.dto.response.MediaPostResponse;
import spring.bbs.post.dto.response.PostListResponse;
import spring.bbs.post.dto.response.PostResponse;
import spring.bbs.post.service.PostService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final MediaService mediaService;

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> readPost(@PathVariable("id") long postId){
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PostListResponse>> lookupPostPage(
            @ModelAttribute PostListRequest req){
        Page<PostListResponse> response = postService.getPostList(req);

        return ResponseEntity.ok(response);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<MediaPostResponse> writePost(@RequestBody MediaPostRequest req) throws IOException{
        MediaPostResponse response = postService.createPost(req);
        if(req.getMediaFiles() != null && !req.getMediaFiles().isEmpty()){
            List<MediaResponse> mediaResponses = mediaService.saveMedia(req.getMediaFiles(), response.getId());
            response.setFiles(mediaResponses);
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deletePost(@PathVariable("id") long postId){
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<PostResponse> modifyPost(@RequestBody PostRequest req,
                                                   @PathVariable("id") long postId){
        PostResponse response = postService.updatePost(req, postId);
        return ResponseEntity.ok(response);
    }
}
