package spring.bbs.written.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.bbs.category.domain.Category;
import spring.bbs.member.domain.Member;
import spring.bbs.written.comment.domain.Comment;
import spring.bbs.written.domain.Written;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post extends Written {
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    private Category category;
    @OneToMany(mappedBy="post", orphanRemoval = true)
    List<Comment> commentList;

    public Post(String title, String content, LocalDateTime createdTime, LocalDateTime modifiedTime, Member author, Category category) {
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.author = author;
        this.category = category;
    }
}