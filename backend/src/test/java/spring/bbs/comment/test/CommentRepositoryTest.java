package spring.bbs.comment.test;

import jakarta.persistence.EntityManager;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.repository.CommentRepository;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.repository.PostRepository;
import spring.bbs.util.RoleType;
import spring.config.TestConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepositoryHandler categoryRepositoryHandler;
    @Autowired
    private EntityManager em;

    private final int PAGE_SIZE = 20;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("게시글 번호, 댓글 페이지 번호를 주면 해당 게시글의 댓글 목록을 페이지에 맞게 반환한다.")
    void findAllToPageByPost() {
        //given
        Member member = createMember("CommentTestUser");
        Post post = createPost(member);
        createCommentList(post, member, PAGE_SIZE);
        createComment(post, member, "RightPageComment1");
        createComment(post, member, "RightPageComment2");
        createComment(post, member, "RightPageComment3");
        int page = 2;
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").ascending());
        //when
        PageImpl<Comment> result = commentRepository.findAllByPost(post, pageable);
        //then
        assertThat(result.get().collect(Collectors.toList())).hasSize(3)
            .extracting("content")
            .containsExactly(
                "RightPageComment1",
                "RightPageComment2",
                "RightPageComment3"
            );
    }

    @Test
    @DisplayName("게시글 번호, 댓글 페이지 번호, 검색 키워드를 주면 해당 게시글의 키워드가 포함된 댓글 목록을 페이지에 맞게 반환한다.")
    void findAllToPageByPostAndSearchkeyword() {
        //given
        Member member = createMember("CommentTestUser");
        Post post = createPost(member);
        createComment(post, member, "test1");
        createComment(post, member, "test2");
        createComment(post, member, "hellohihello");
        int page = 1;
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").ascending());
        //when
        PageImpl<Comment> result = commentRepository.findAllByPostAndSearchKeyword(post, "hi", pageable);
        //then
        assertThat(result.get().collect(Collectors.toList())).hasSize(1)
            .extracting("content")
            .containsExactlyInAnyOrder(
                "hellohihello"
            );
    }

    @DisplayName("동일한 부모를 가진 댓글 중 가장 최근에 작성된 댓글의 order를 반환한다.")
    @Test
    void findLatestCreatedCommentOrderWithSameParent(){
        //given
        Member member = createMember("CommentTestUser");
        Post post = createPost(member);
        Comment comment1 = createComment(post, member, "test1");
        createComment(post, member, "test2", comment1, 1);
        createComment(post, member, "test2", comment1, 2);
        createComment(post, member, "test2", comment1, 3);
        //when
        int order = commentRepository.findLatestOrderWithSameParent(comment1);
        //then
        assertThat(order).isEqualTo(3);
    }

    @DisplayName("동일한 부모를 가진 댓글이 없으면 0을 반환한다.")
    @Test
    void findLatestCreatedCommentOrderWithNoSameParent(){
        //given
        Member member = createMember("CommentTestUser");
        Post post = createPost(member);
        Comment comment1 = createComment(post, member, "test1");
        //when
        int order = commentRepository.findLatestOrderWithSameParent(comment1);
        //then
        assertThat(order).isEqualTo(0);
    }

    @DisplayName("대댓글 생성 시 같은 댓글을 부모로 가지는 대댓글 중 생성할 대댓글의 후 순서인 대댓글의 순서를 변경한다.")
    @Test
    void updateOrder(){
        //given
        Member member = createMember("CommentTestUser");
        Post post = createPost(member);
        Comment parentComment = createComment(post, member, "parentComment");
        Comment childComment1 = createComment(post, member, "childComment1", parentComment, 1);
        Comment childComment2 = createComment(post, member, "childComment2", parentComment, 2);
        Comment childComment3 = createComment(post, member, "childComment3", parentComment, 3);
//        Comment grandChildComment = createComment(post, member, "grandChildComment", childComment1, 2);
        commentRepository.flush();
        //when
        commentRepository.updateOrder(post, parentComment.getId(), 2);
        //then
        em.flush();
        em.clear();
        List<Comment> result = commentRepository.findAll();

        assertThat(result).hasSize(4)
            .extracting("content", "groupOrder")
            .containsExactlyInAnyOrder(
                Tuple.tuple(parentComment.getContent(), 0),
                Tuple.tuple(childComment1.getContent(), 1),
                Tuple.tuple(childComment2.getContent(), 3),
                Tuple.tuple(childComment3.getContent(), 4)
            );
    }

    private List<Comment> createCommentList(Post post, Member author, int num) {
        List<Comment> commentList = new ArrayList<>(num);
        for (int i = 1; i <= num; i++) {
            commentList.add(Comment.builder()
                .content("testContent" + i)
                .author(author)
                .post(post)
                .parentComment(null)
                .build());
        }
        return commentRepository.saveAllAndFlush(commentList);
    }

    private Comment createComment(Post post, Member author, String content, Comment parentComment, int order) {
        Comment comment = Comment.of(content, author, post, parentComment, order);
        return commentRepository.save(comment);
    }

    private Comment createComment(Post post, Member author, String content) {
        Comment comment = Comment.of(content, author, post);
        return commentRepository.save(comment);
    }

    private Post createPost(Member author) {
        PostRequest req = new PostRequest(
            "createTestTitle", "createTestContent", "string");
        Post post = Post.of(req, categoryRepositoryHandler.findByName(req.getCategory()), author);
        return postRepository.save(post);
    }

    private Member createMember(String name) {
        Member newMember = Member.builder()
            .name(name)
            .password("password")
            .email(name + "@test.com")
            .isEnabled(true)
            .authority(Enum.valueOf(Authority.class, RoleType.user))
            .build();
        return memberRepository.save(newMember);
    }
}