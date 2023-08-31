package spring.bbs.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.domain.QPost;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory queryFactory;
    QPost p = QPost.post;

    @Override
    public Page<Post> findAll(Pageable pageable) {
        List<Post> postList = queryFactory
            .select(p)
            .from(p)
            .orderBy(p.createdTime.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(postList, pageable, count(null));
    }

    @Override
    public Page<Post> findAllBySearchKeywordAndScope(String scope, String keyword, Pageable pageable) {

        BooleanExpression searchExpression = getSearchScopeAndKeyWordExpression(scope, keyword);

        List<Post> postList = queryFactory
            .selectFrom(p)
            .where(searchExpression)
            .orderBy(p.createdTime.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(postList, pageable, count(searchExpression));
    }

    private BooleanExpression getSearchScopeAndKeyWordExpression(String searchScope, String searchKeyword) {
        BooleanExpression searchExpression;
        if ("제목".equals(searchScope)) {
            searchExpression = p.title.containsIgnoreCase(searchKeyword);
        } else if ("전체".equals(searchScope)) {
            searchExpression = p.title.containsIgnoreCase(searchKeyword)
                .or(p.content.containsIgnoreCase(searchKeyword));
        } else if ("작성자".equals(searchScope)) {
            searchExpression = p.author.name.eq(searchKeyword);
        } else {
            throw new IllegalStateException("해당 검색 범위를 지원하지 않습니다.");
        }
        return searchExpression;
    }

    private Long count(BooleanExpression expression) {
        return queryFactory
            .select(p.count())
            .from(p)
            .where(expression)
            .fetchOne();
    }
}
