package spring.bbs.written.post.dto.util;

import spring.bbs.category.domain.Category;
import spring.bbs.member.domain.Member;
import spring.bbs.written.post.domain.Post;
import spring.bbs.written.post.dto.request.MediaPostRequest;
import spring.bbs.written.post.dto.request.PostRequest;

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
