package spring.bbs.post.dto.util;

import spring.bbs.member.domain.Member;
import spring.bbs.post.domain.Category;
import spring.bbs.post.domain.Post;

import java.time.LocalDateTime;

public class RequestToPost {
    public static Post convertRequestToPost(String title,
                                            String content,
                                            LocalDateTime createdDate,
                                            LocalDateTime modifiedDate,
                                            Member author,
                                            Category category){
        return new Post(title, content, createdDate, modifiedDate, author, category);
    }
}
