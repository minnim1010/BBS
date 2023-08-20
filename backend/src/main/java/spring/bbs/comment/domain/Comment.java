package spring.bbs.comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import spring.bbs.common.entity.Written;
import spring.bbs.member.domain.Member;
import spring.bbs.post.domain.Post;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends Written {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 200, nullable = false)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parentComment;
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE)
    private List<Comment> childComments = new ArrayList<>();
    @Column(nullable = false)
    private boolean isDeleted;
    @Column(nullable = false)
    private boolean canDeleted;
    @Column(nullable = false)
    private int groupOrder;
    private Long groupNum;

    public static final String DELETED_CONTENT = "삭제된 댓글입니다.";

    @Builder
    private Comment(
        String content,
        Member author,
        Post post,
        Comment parentComment,
        boolean isDeleted,
        int groupOrder,
        Long groupNum,
        boolean canDeleted) {
        Assert.hasText(content, "content must be provided");
        Assert.notNull(author, "author must be provided");
        Assert.notNull(post, "post must be provided");

        this.content = content;
        this.author = author;
        this.post = post;
        this.parentComment = parentComment;
        this.isDeleted = isDeleted;
        this.groupOrder = groupOrder;
        this.groupNum = groupNum;
        this.canDeleted = canDeleted;
    }

    public static Comment of(String content,
                             Member author,
                             Post post) {
        return Comment.builder()
            .content(content)
            .author(author)
            .post(post)
            .parentComment(null)
            .groupOrder(0)
            .groupNum(null)
            .isDeleted(false)
            .canDeleted(true)
            .build();
    }


    public static Comment of(String content,
                             Member author,
                             Post post,
                             Comment parentComment,
                             int groupOrder) {
        if (parentComment == null) {
            throw new IllegalStateException("대댓글을 달 수 있는 댓글이 없습니다.");
        }

        if (parentComment.getGroupNum() == null) {
            parentComment.updateGroupNum(parentComment.getId());
        }
        Long groupNum = parentComment.getGroupNum();
        parentComment.setCanDeleted(false);

        return Comment.builder()
            .content(content)
            .author(author)
            .post(post)
            .parentComment(parentComment)
            .groupOrder(groupOrder)
            .groupNum(groupNum)
            .isDeleted(false)
            .canDeleted(true)
            .build();
    }

    public void setCanDeleted(boolean hasNoChild) {
        canDeleted = hasNoChild;
    }

    public Comment update(String content) {
        this.content = content;
        return this;
    }

    public void delete() {
        isDeleted = true;
        content = DELETED_CONTENT;
    }

    public void updateGroupNum(Long groupNum) {
        this.groupNum = groupNum;
    }
}
