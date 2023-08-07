package spring.bbs.written.post.dto.response;

import spring.bbs.member.dto.response.MemberResponse;

import java.time.LocalDateTime;

public class PostResponse {
    private long id;
    private String title;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private MemberResponse authorResponse;
    private String category;

    public PostResponse() {
    }

    public PostResponse(long id,
                        String title,
                        String content,
                        LocalDateTime createdTime,
                        LocalDateTime modifiedTime,
                        MemberResponse author,
                        String category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.authorResponse = author;
        this.category = category;
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

    public MemberResponse getAuthorResponse() {
        return authorResponse;
    }

    public void setAuthorResponse(MemberResponse authorResponse) {
        this.authorResponse = authorResponse;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}