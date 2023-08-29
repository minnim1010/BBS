//package spring.bbs.comment.repository;
//
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//import spring.bbs.comment.domain.QComment;
//
//@Repository
//@RequiredArgsConstructor
//public class CommentCustomRepositoryImpl implements CommentCustomRepository {
//
//    private final JPAQueryFactory queryFactory;
//    QComment comment = QComment.comment;
//
//    @Override
//    public List<Comment> findAllByPost(Post post) {
//        List<Comment> comments = queryFactory
//            .selectDistinct(comment)
//            .from(comment)
//            .leftJoin(comment.children).fetchJoin()
//            .where(comment.post.eq(post))
//            .fetch();
//
//        return comments.stream()
//            .filter(c -> c.getParent() == null)
//            .toList();
//    }
//}
