package spring.bbs.post.dto.response;

import spring.bbs.member.domain.Member;

import java.time.LocalDateTime;

public class PostListResponse {
    private String title;
    private LocalDateTime createdTime;
    private Member author;

    public PostListResponse() {
    }

    public PostListResponse(String title, LocalDateTime createdTime, Member author) {
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
