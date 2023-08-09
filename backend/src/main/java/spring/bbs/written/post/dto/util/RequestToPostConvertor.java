package spring.bbs.written.post.dto.util;

import spring.bbs.category.domain.Category;
import spring.bbs.member.domain.Member;
import spring.bbs.written.post.domain.Post;
import spring.bbs.written.post.dto.request.MediaPostRequest;
import spring.bbs.written.post.dto.request.PostRequest;

import java.time.LocalDateTime;

public class RequestToPostConvertor {
    public static Post of(MediaPostRequest req,
                               Member author,
                               Category category) {
        return new Post(req.getTitle(), req.getContent(), LocalDateTime.now(), null, author, category);
    }

    public static Post of(PostRequest req,
                          Member author,
                          Category category) {
        return new Post(req.getTitle(), req.getContent(), LocalDateTime.now(), null, author, category);
    }
}
