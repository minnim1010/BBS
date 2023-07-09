package spring.bbs.comment.util;

import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.dto.response.CommentResponse;

public class CommentToResponse {

    private CommentToResponse(){}

    public static CommentResponse convertCommentToResponse(Comment comment){
        Comment parentComment = comment.getParentComment();
        Long parentCommentId = parentComment == null ? null : parentComment.getId();
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedTime(),
                comment.getModifiedTime(),
                comment.getAuthor().getName(),
                parentCommentId
        );
    }
}
