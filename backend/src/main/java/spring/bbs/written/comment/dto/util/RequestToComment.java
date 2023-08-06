package spring.bbs.written.comment.dto.util;

import spring.bbs.member.domain.Member;
import spring.bbs.written.comment.domain.Comment;
import spring.bbs.written.post.domain.Post;

import java.time.LocalDateTime;

public class RequestToComment {

    private RequestToComment(){}

    public static Comment convertRequestToComment(String content,
                                          Member author,
                                          Post post,
                                          Comment parentComment) {

        return new Comment(content, LocalDateTime.now(), null, author, post, parentComment);
    }
}
