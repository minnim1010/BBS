package spring.bbs.post.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.bbs.base.domain.Written;
import spring.bbs.category.domain.Category;
import spring.bbs.comment.domain.Comment;
import spring.bbs.member.domain.Member;
import spring.bbs.post.dto.request.PostRequest;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends Written {
    @Id @GeneratedValue
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    private Category category;
    @OneToMany(mappedBy="post", orphanRemoval = true)
    List<Comment> commentList;

    @Builder
    private Post(String title, String content, Member author, Category category) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
    }

    public static Post of(PostRequest req, Category category, Member author) {
        return Post.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .category(category)
                .author(author)
                .build();
    }

    public Post update(String title, String content, Category category){
        this.title = title;
        this.content = content;
        this.category = category;
        return this;
    }
}
