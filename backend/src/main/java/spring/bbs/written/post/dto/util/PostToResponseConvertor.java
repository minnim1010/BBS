package spring.bbs.written.post.dto.util;

import spring.bbs.member.dto.response.MemberResponse;
import spring.bbs.written.post.domain.Post;
import spring.bbs.written.post.dto.response.MediaPostResponse;
import spring.bbs.written.post.dto.response.PostListResponse;
import spring.bbs.written.post.dto.response.PostResponse;

public class PostToResponseConvertor {
    public static PostResponse toPostResponse(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent(),
                post.getCreatedTime(), post.getLastModifiedTime(),
                new MemberResponse(post.getAuthor().getId(), post.getAuthor().getName()),
                post.getCategory().getName());
    }

    public static MediaPostResponse toMediaPostResponse(Post post) {
        return new MediaPostResponse(post.getId(), post.getTitle(), post.getContent(),
                post.getCreatedTime(), post.getLastModifiedTime(),
                new MemberResponse(post.getAuthor().getId(), post.getAuthor().getName()),
                post.getCategory().getName(), null);
    }

    public static PostListResponse toPostListResponse(Post post){
        return new PostListResponse(post.getId(), post.getTitle(), post.getCreatedTime(),
                new MemberResponse(post.getAuthor().getId(), post.getAuthor().getName())
        );
    }
}
