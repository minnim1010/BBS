package spring.bbs.written.domain;

import jakarta.persistence.*;
import spring.bbs.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Written {
    @Id @GeneratedValue
    protected Long id;
    protected LocalDateTime createdTime;
    protected LocalDateTime modifiedTime;
    @ManyToOne
    protected Member author;

    public Long getId() {
        return id;
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
}
