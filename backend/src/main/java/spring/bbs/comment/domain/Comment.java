package spring.bbs.comment.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import spring.bbs.common.entity.BaseTime;
import spring.bbs.member.domain.Member;
import spring.bbs.post.domain.Post;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    @NotNull
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Builder
    private Comment(
        String content,
        Member author,
        Post post,
        Comment parent) {
        Assert.hasText(content, "content must be provided");
        Assert.notNull(author, "author must be provided");
        Assert.notNull(post, "post must be provided");

        this.content = content;
        this.author = author;
        this.post = post;
        this.parent = parent;
    }

    public static Comment create(@NonNull String content,
                                 @NonNull Member author,
                                 @NonNull Post post) {
        return Comment.builder()
            .content(content)
            .author(author)
            .post(post)
            .parent(null)
            .build();
    }

    public static Comment createReply(@NonNull String content,
                                      @NonNull Member author,
                                      @NonNull Post post,
                                      @NonNull Comment parent) {
        return Comment.builder()
            .content(content)
            .author(author)
            .post(post)
            .parent(parent)
            .build();
    }

    public Comment update(@NonNull String content) {
        this.content = content;
        return this;
    }

    public void addChildren(@NonNull Comment child) {
        children.add(child);
        child.parent = this;
    }
}
