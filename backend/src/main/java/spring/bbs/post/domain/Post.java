package spring.bbs.post.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import spring.bbs.category.domain.Category;
import spring.bbs.comment.domain.Comment;
import spring.bbs.common.entity.BaseTime;
import spring.bbs.member.domain.Member;
import spring.bbs.post.service.dto.PostServiceRequest;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @Column(columnDefinition = "TEXT")
    @NotNull
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member author;

    @ManyToOne
    @NotNull
    private Category category;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    List<Comment> commentList = new ArrayList<>();

    @Builder
    private Post(String title, String content, Member author, Category category) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
    }

    public static Post of(PostServiceRequest req, Category category, Member author) {
        Assert.hasText(req.getTitle(), "title must be provided");
        Assert.hasText(req.getContent(), "content must be provided");
        Assert.notNull(author, "author must be provided");
        Assert.notNull(category, "category must be provided");

        return Post.builder()
            .title(req.getTitle())
            .content(req.getContent())
            .category(category)
            .author(author)
            .build();
    }

    public Post update(String title, String content, Category category) {
        Assert.hasText(title, "title must be provided");
        Assert.hasText(content, "content must be provided");
        Assert.notNull(category, "category must be provided");

        this.title = title;
        this.content = content;
        this.category = category;
        return this;
    }
}
