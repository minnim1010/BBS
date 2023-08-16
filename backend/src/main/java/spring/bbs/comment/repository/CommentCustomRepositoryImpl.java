package spring.bbs.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.domain.QComment;
import spring.bbs.post.domain.Post;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;
    QComment c = QComment.comment;

    @Override
    public PageImpl<Comment> findAllByPost(Post post, Pageable pageable) {
        List<Comment> commentList = queryFactory
            .select(c)
            .from(c)
            .where(c.post.eq(post))
            .groupBy(c.groupNum, c.id)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        commentList.sort((Comment c1, Comment c2) -> {
            if(c1.getGroupNum() == c2.getGroupNum())
                return c1.getGroupOrder() - c2.getGroupOrder();
            return c1.getGroupOrder() - c2.getGroupOrder();
        });

        Long count = countsByPost(post);

        return new PageImpl<>(commentList, pageable, count);
    }

    @Override
    public PageImpl<Comment> findAllByPostAndSearchKeyword(Post post, String searchKeyword, Pageable pageable) {
        List<Comment> commentList = queryFactory
            .select(c)
            .from(c)
            .where(c.post.eq(post).and(c.content.contains(searchKeyword)))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long count = countsByPost(post);

        return new PageImpl<>(commentList, pageable, count);
    }

    @Override
    public void updateOrder(Post post, Long groupNum, int updateStartOrder) {
        queryFactory
            .update(c)
            .set(c.groupOrder, c.groupOrder.add(1))
            .where(c.post.eq(post)
                    .and(c.groupNum.eq(groupNum))
                    .and(c.groupOrder.goe(updateStartOrder)))
            .execute();
    }

    @Override
    public int findLatestOrderWithSameParent(Comment parentComment) {
        try{
            return queryFactory
                .select(c.groupOrder)
                .from(c)
                .where(c.parentComment.eq(parentComment))
                .orderBy(c.createdTime.desc())
                .limit(1)
                .fetchOne();
        }catch (NullPointerException e){
            return 0;
        }
    }

    private Long countsByPost(Post post) {
        return queryFactory
            .select(c.count())
            .from(c)
            .where(c.post.eq(post))
            .fetchOne();
    }
}
