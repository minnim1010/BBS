package spring.bbs.comment.dto.response;

import spring.bbs.comment.domain.Comment;
import spring.bbs.member.domain.Member;
import spring.bbs.post.domain.Post;

import java.time.LocalDateTime;

public class CommentResponse {
    private long id;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private Member author;
    private Post postId;
    private Comment parentComment;

    public CommentResponse() {
    }

    public CommentResponse(long id,
                                 String content,
                                 LocalDateTime createdTime,
                                 LocalDateTime modifiedTime,
                                 Member author,
                                 Post postId,
                                 Comment parentComment) {
        this.id = id;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.author = author;
        this.postId = postId;
        this.parentComment = parentComment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Member getAuthor() {
        return author;
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    public Post getPostId() {
        return postId;
    }

    public void setPostId(Post postId) {
        this.postId = postId;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }
}
