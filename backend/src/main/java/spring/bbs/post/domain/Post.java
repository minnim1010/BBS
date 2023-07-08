package spring.bbs.post.domain;

import jakarta.persistence.*;
import spring.bbs.member.domain.Member;

import java.time.LocalDateTime;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    @ManyToOne
    private Member author;
    @ManyToOne
    private Category category;

    public Post(String title,
                String content,
                LocalDateTime createdTime,
                LocalDateTime modifiedTime,
                Member author,
                Category category) {
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.author = author;
        this.category = category;
    }

    public Post() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
