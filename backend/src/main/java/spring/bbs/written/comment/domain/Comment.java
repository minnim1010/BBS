package spring.bbs.written.comment.domain;

import jakarta.persistence.*;
import spring.bbs.member.domain.Member;
import spring.bbs.written.domain.Written;
import spring.bbs.written.post.domain.Post;

import java.time.LocalDateTime;

@Entity
public class Comment extends Written {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Post post;
    @OneToOne
    private Comment parentComment;

    public Comment() {
    }

    public Comment(String content,
                   LocalDateTime createdTime,
                   LocalDateTime modifiedTime,
                   Member author,
                   Post post,
                   Comment parentComment) {
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.author = author;
        this.post = post;
        this.parentComment = parentComment;
    }

    public Long getId() {
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getParentComment() {
        return parentComment;
    }
}
