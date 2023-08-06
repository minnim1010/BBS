package spring.bbs.written.post.dto.response;

import spring.bbs.member.dto.response.MemberResponse;

import java.time.LocalDateTime;

public class PostListResponse {
    private long id;
    private String title;
    private LocalDateTime createdTime;
    private MemberResponse authorResponse;

    public PostListResponse() {
    }

    public PostListResponse(long id, String title, LocalDateTime createdTime, MemberResponse memberResponse) {
        this.id = id;
        this.title = title;
        this.createdTime = createdTime;
        this.authorResponse = memberResponse;
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

    public MemberResponse getAuthorResponse() {
        return authorResponse;
    }

    public void setAuthorResponse(MemberResponse authorResponse) {
        this.authorResponse = authorResponse;
    }
}
