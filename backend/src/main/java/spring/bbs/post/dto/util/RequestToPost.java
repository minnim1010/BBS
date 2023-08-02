package spring.bbs.post.dto.util;

import spring.bbs.member.domain.Member;
import spring.bbs.post.domain.Category;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.request.MediaPostRequest;
import spring.bbs.post.dto.request.PostRequest;

import java.time.LocalDateTime;

public class RequestToPost {
    public static Post convertCreateRequestToPost(PostRequest req,
                                            Member author,
                                            Category category){
        return new Post(req.getTitle(), req.getContent(), LocalDateTime.now(), null, author, category);
    }

    public static Post convertCreateRequestToPost(MediaPostRequest req,
                                                  Member author,
                                                  Category category){
        return new Post(req.getTitle(), req.getContent(), LocalDateTime.now(), null, author, category);
    }
}
