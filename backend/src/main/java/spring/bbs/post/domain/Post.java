package spring.bbs.post.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.bbs.comment.domain.Comment;
import spring.bbs.member.domain.Member;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
