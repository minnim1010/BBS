package spring.bbs.repository;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.IntegrationTestConfig;
import spring.bbs.category.domain.Category;
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.repository.CommentRepository;
import spring.bbs.comment.repository.dto.CommentResponse;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class CommentRepositoryTest extends IntegrationTestConfig {

    private static final String MEMBER_NAME = "CommentTestUser";

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepositoryHandler categoryRepositoryHandler;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("게시글 번호를 주면 ")
    @Nested
    class FindAllByPost {
        @DisplayName("답글 없는 댓글만 있는 게시글의 댓글 목록을 반환한다.")
        @Test
        void successWithNoReplyComments() {
            //given
            Member member = createMember("member");

            Post post = createPost(member);

            createComment(post, member, "RightPageComment1");
            createComment(post, member, "RightPageComment2");
            createComment(post, member, "RightPageComment3");

            //when
            List<CommentResponse> result = commentRepository.findAllByPost(post.getId());

            //then
            assertThat(result).hasSize(3)
                .extracting("content", "repliesCount")
                .containsExactly(
                    Tuple.tuple("RightPageComment1", 0),
                    Tuple.tuple("RightPageComment2", 0),
                    Tuple.tuple("RightPageComment3", 0)
                );
        }

        @DisplayName("답글 있는 댓글이 있는 게시글의 답글이 아닌 댓글 목록을 반환한다.")
        @Test
        void successWithReplyComments() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member);

            Comment grandParentComment1 = createComment(post, member, "GrandParentComment1");
            createReplyComment(post, member, "ParentComment1", grandParentComment1);

            Comment grandParentComment2 = createComment(post, member, "GrandParentComment2");
            createReplyComment(post, member, "ParentComment2", grandParentComment2);
            createReplyComment(post, member, "ParentComment3", grandParentComment2);

            //when
            List<CommentResponse> result = commentRepository.findAllByPost(post.getId());

            //then
            assertThat(result).hasSize(2)
                .extracting("content", "repliesCount")
                .containsExactly(
                    Tuple.tuple("GrandParentComment1", 1),
                    Tuple.tuple("GrandParentComment2", 2)
                );
        }
    }

    @DisplayName("부모 댓글 번호를 줄 때 ")
    @Nested
    class FindAllByParent {
        @DisplayName("해당 댓글 번호를 부모 댓글로 가지는 모든 댓글을 조회한다.")
        @Test
        void successReturnCommentsWithSpecificParentCommentId() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member);

            Comment grandParentComment1 = createComment(post, member, "GrandParentComment1");
            Comment parentComment1 = createReplyComment(post, member, "ParentComment1", grandParentComment1);
            Comment parentComment2 = createReplyComment(post, member, "ParentComment2", grandParentComment1);

            //when
            List<CommentResponse> result = commentRepository.findAllByParent(grandParentComment1);

            //then
            assertThat(result).hasSize(2)
                .extracting("content", "repliesCount")
                .containsExactly(
                    Tuple.tuple("ParentComment1", 0),
                    Tuple.tuple("ParentComment2", 0)
                );
        }
    }

    @DisplayName("댓글을 삭제할 때 ")
    @Nested
    class Delete {
        @DisplayName("댓글에 답글이 달려있으면 댓글과 관련 답글을 모두 지운다.")
        @Test
        void success() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member);

            Comment parentComment1 = createComment(post, member, "parentComment1");
            Comment comment1 = createReplyComment(post, member, "comment1", parentComment1);
            Comment comment2 = createReplyComment(post, member, "comment2", parentComment1);
            parentComment1.addChildren(comment1);
            parentComment1.addChildren(comment2);

            //when
            commentRepository.delete(parentComment1);

            //then
            List<Comment> result = commentRepository.findAll();
            assertThat(result).isEmpty();
        }
    }

    private List<Comment> createCommentList(Post post, Member author, int num) {
        List<Comment> commentList = IntStream.rangeClosed(1, num)
            .mapToObj(i -> Comment.builder()
                .content("testContent" + i)
                .author(author)
                .post(post)
                .parent(null)
                .build())
            .collect(Collectors.toList());

        return commentRepository.saveAll(commentList);
    }

    private Comment createReplyComment(Post post, Member author, String content, Comment parent) {
        Comment comment = Comment.createReply(content, author, post, parent);
        return commentRepository.save(comment);
    }

    private Comment createComment(Post post, Member author, String content) {
        Comment comment = Comment.create(content, author, post);
        return commentRepository.save(comment);
    }

    private Post createPost(Member member) {
        Category category = categoryRepositoryHandler.findByName("string");

        Post post = Post.builder()
            .author(member)
            .title("title")
            .content("content")
            .category(category)
            .build();

        return postRepository.save(post);
    }

    private Member createMember(String name) {
        Member newMember = Member.builder()
            .name(name)
            .password(name)
            .email(name + "@test.com")
            .authority(Authority.ROLE_USER)
            .build();

        return memberRepository.save(newMember);
    }
}