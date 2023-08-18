package spring.bbs.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
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

import static spring.bbs.util.QuerydslUtil.getOrderSpecifier;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostCustomRepositoryImpl implements PostCustomRepository{
    private final JPAQueryFactory queryFactory;
    QPost p = QPost.post;

    @Override
    public Page<Post> findAllToPage(Pageable pageable) {
        List<Post> postList = queryFactory
            .select(p)
            .from(p)
            .orderBy(getOrderSpecifier(
                pageable.getSort(), new PathBuilder(Post.class, "post")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(postList, pageable, count());
    }

    @Override
    public Page<Post> findAllToPageAndSearchKeywordAndScope(String scope, String keyword, Pageable pageable) {

        BooleanExpression searchExpression = getSearchScopeAndKeyWordExpression(scope, keyword);

        List<Post> postList = queryFactory
            .selectFrom(p)
            .where(searchExpression)
            .orderBy(getOrderSpecifier(
                pageable.getSort(), new PathBuilder(Post.class, "post")))
            .fetch();

        return new PageImpl<>(postList, pageable, count());
    }

    private BooleanExpression getSearchScopeAndKeyWordExpression(String scope, String keyword) {
        BooleanExpression searchExpression;
        if("제목".equals(scope)){
            searchExpression = p.title.containsIgnoreCase(keyword);
        } else if ("전체".equals(scope)) {
            searchExpression = p.title.containsIgnoreCase(keyword)
                .or(p.content.containsIgnoreCase(keyword));
        } else if ("작성자".equals(scope)) {
            searchExpression = p.author.name.eq(keyword);
        }else {
            throw new IllegalStateException("해당 검색 범위를 지원하지 않습니다.");
        }
        return searchExpression;
    }

    private Long count(){
        return queryFactory
            .select(p.count())
            .from(p)
            .fetchOne();
    }
}
