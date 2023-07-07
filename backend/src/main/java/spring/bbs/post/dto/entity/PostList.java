package spring.bbs.post.dto.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import spring.bbs.member.domain.Member;

import java.time.LocalDateTime;

@Entity
public class PostList {
    @Id
    private long id;
    private String title;
    private LocalDateTime createdTime;
    @ManyToOne
    private Member author;

    public PostList() {
    }

    public PostList(String title, LocalDateTime createdTime, Member author) {
        this.title = title;
        this.createdTime = createdTime;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public Member getAuthor() {
        return author;
    }

    public void setAuthor(Member author) {
        this.author = author;
    }
}
