package spring.bbs.comment.dto.response;

import spring.bbs.member.dto.response.MemberNameResponse;

import java.time.LocalDateTime;

public class CommentResponse {
    private long id;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private MemberNameResponse author;
    private Long parentCommentId;

    public CommentResponse() {
    }

    public CommentResponse(long id,
                                 String content,
                                 LocalDateTime createdTime,
                                 LocalDateTime modifiedTime,
                                 String authorName,
                                 Long parentCommentId) {
        this.id = id;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.author = new MemberNameResponse(authorName);
        this.parentCommentId = parentCommentId;
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

    public MemberNameResponse getAuthor() {
        return author;
    }

    public void setAuthor(MemberNameResponse author) {
        this.author = author;
    }

    public Long getParentComment() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}
