package spring.bbs.comment.util;

import spring.bbs.comment.domain.Comment;
import spring.bbs.member.domain.Member;
import spring.bbs.post.domain.Post;

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
