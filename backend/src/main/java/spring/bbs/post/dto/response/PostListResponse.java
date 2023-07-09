package spring.bbs.post.dto.response;

import spring.bbs.member.dto.response.MemberNameResponse;

import java.time.LocalDateTime;

public class PostListResponse {
    private long id;
    private String title;
    private LocalDateTime createdTime;
    private MemberNameResponse author;

    public PostListResponse() {
    }

    public PostListResponse(long id, String title, LocalDateTime createdTime, MemberNameResponse memberResponse) {
        this.id = id;
        this.title = title;
        this.createdTime = createdTime;
        this.author = memberResponse;
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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public MemberNameResponse getAuthor() {
        return author;
    }

    public void setAuthor(MemberNameResponse author) {
        this.author = author;
    }
}
