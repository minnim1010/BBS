package spring.bbs.post.dto.util;

import spring.bbs.member.dto.response.MemberNameResponse;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.response.PostListResponse;
import spring.bbs.post.dto.response.PostResponse;

public class PostToResponse {
    public static PostResponse convertPostToResponse(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent(),
                post.getCreatedTime(), post.getModifiedTime(), post.getAuthor(), post.getCategory().getName());
    }

    public static PostListResponse convertPostToPostListResponse(Post post){
        return new PostListResponse(post.getId(), post.getTitle(), post.getCreatedTime(),
                new MemberNameResponse(post.getAuthor().getName())
        );
    }
}
