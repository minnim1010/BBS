package spring.bbs.comment.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import spring.bbs.base.domain.Written;
import spring.bbs.member.domain.Member;
import spring.bbs.post.domain.Post;

@Entity
@Getter
public class Comment extends Written {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 200)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Post post;
    @OneToOne
    private Comment parentComment;

    public Comment() {
    }

    @Builder
    private Comment(long id, String content, Member author, Post post, Comment parentComment) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.post = post;
        this.parentComment = parentComment;
    }

    public static Comment of(String content,
                             Member author,
                             Post post,
                             Comment parentComment){
        return Comment.builder()
                .content(content)
                .author(author)
                .post(post)
                .parentComment(parentComment)
                .build();
    }

    public Comment update(String content){
        this.content = content;
        return this;
    }
}
