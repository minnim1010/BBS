package spring.bbs.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import spring.IntegrationTestConfig;
import spring.bbs.category.domain.Category;
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.comment.controller.dto.response.CommentResponse;
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.repository.CommentRepository;
import spring.bbs.comment.service.CommentService;
import spring.bbs.comment.service.dto.CommentCreateServiceRequest;
import spring.bbs.comment.service.dto.CommentDeleteServiceRequest;
import spring.bbs.comment.service.dto.CommentListServiceRequest;
import spring.bbs.comment.service.dto.CommentUpdateServiceRequest;
import spring.bbs.common.exceptionhandling.exception.DataNotFoundException;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommentServiceTest extends IntegrationTestConfig {
    private static final String MEMBER_NAME = "CommentTestUser";

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepositoryHandler categoryRepositoryHandler;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("댓글의 답글을 요청할 때 ")
    class GetCommentsByParent {
        @DisplayName("해당 댓글의 답글 목록을 반환한다.")
        @Test
        void test() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            Comment grandParentComment1 = createComment("GrandParentComment1", member, post, null);
            Comment parentComment1 = createComment("parentComment1", member, post, grandParentComment1);
            Comment parentComment2 = createComment("parentComment2", member, post, grandParentComment1);

            //when
            List<CommentResponse> replies = commentService.getCommentsByParent(grandParentComment1.getId());

            //then
            assertThat(replies).hasSize(2)
                .extracting("content")
                .contains("parentComment1", "parentComment2");
        }
    }

    @Nested
    @DisplayName("특정 게시글의 댓글 목록을 요청할 때 ")
    class GetCommentsByPost {
        @DisplayName("댓글 목록을 반환한다.")
        @Test
        void successReturnAllCommentList() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            Comment grandParentComment1 = createComment("grandParentComment1", member, post, null);
            Comment parentComment1 = createComment("parentComment1", member, post, grandParentComment1);
            Comment childComment1 = createComment("childComment1", member, post, parentComment1);
            Comment parentComment2 = createComment("parentComment2", member, post, grandParentComment1);
            Comment childComment2 = createComment("childComment2", member, post, parentComment1);
            Comment grandParentComment2 = createComment("grandParentComment2", member, post, null);

            CommentListServiceRequest request = CommentListServiceRequest.builder()
                .postId(post.getId())
                .build();

            //when
            List<CommentResponse> response = commentService.getCommentsByPost(request);

            //then
            assertThat(response).hasSize(2)
                .extracting("content")
                .containsExactly("grandParentComment1", "grandParentComment2");
        }
    }

    @Nested
    @DisplayName("댓글 작성 요청이 들어오면")
    class createComment {
        @DisplayName("답글이 아닌 댓글을 작성한다.")
        @Test
        void successReturnNewComment() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            CommentCreateServiceRequest request = CommentCreateServiceRequest.builder()
                .postId(post.getId())
                .content("comment")
                .parentCommentId(null)
                .curMemberName(MEMBER_NAME)
                .build();

            //when
            CommentResponse response = commentService.createComment(request);

            //then
            assertThat(response)
                .extracting("content", "author.name")
                .contains("comment", MEMBER_NAME);
        }

        @DisplayName("존재하는 댓글에 답글을 작성한다.")
        @Test
        void successReturnNewReplyWithComment() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            Comment parentComment = createComment("parentComment", member, post, null);

            CommentCreateServiceRequest request = CommentCreateServiceRequest.builder()
                .postId(post.getId())
                .content("comment")
                .parentCommentId(parentComment.getId())
                .curMemberName(MEMBER_NAME)
                .build();

            //when
            CommentResponse response = commentService.createComment(request);

            //then
            assertThat(response)
                .extracting("content", "author.name")
                .contains("comment", MEMBER_NAME);
        }

        @DisplayName("유효한 사용자가 아니면 댓글을 작성할 수 없다.")
        @Test
        void failWithInvalidUser() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            CommentCreateServiceRequest request = CommentCreateServiceRequest.builder()
                .postId(post.getId())
                .content("comment")
                .parentCommentId(null)
                .curMemberName("invalidMember")
                .build();

            //when then
            assertThatThrownBy(() ->
                commentService.createComment(request))
                .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("댓글 수정 요청이 들어올 때 ")
    class updateComment {
        @DisplayName("댓글을 수정한다.")
        @Test
        void successReturnUpdatedComment() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            Comment comment = createComment("content", member, post, null);

            CommentUpdateServiceRequest request = CommentUpdateServiceRequest.builder()
                .commentId(comment.getId())
                .content("update")
                .curMemberName(MEMBER_NAME)
                .build();

            //when
            CommentResponse response = commentService.updateComment(request);

            //then
            assertThat(response)
                .extracting("content", "author.name")
                .contains("update", MEMBER_NAME);
        }

        @DisplayName("댓글이 존재하지 않으면 댓글을 수정할 수 없다.")
        @Test
        void failWithNonExistComment() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            CommentUpdateServiceRequest request = CommentUpdateServiceRequest.builder()
                .commentId(1L)
                .content("update")
                .curMemberName(MEMBER_NAME)
                .build();

            //when then
            assertThatThrownBy(() -> commentService.updateComment(request))
                .isInstanceOf(DataNotFoundException.class);
        }

        @DisplayName("유효한 사용자가 아니면 댓글을 수정할 수 없다.")
        @Test
        void failWithInvalidUser() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            Comment comment = createComment("content", member, post, null);

            CommentUpdateServiceRequest request = CommentUpdateServiceRequest.builder()
                .commentId(comment.getId())
                .content("update")
                .curMemberName("invalidMember")
                .build();

            //when then
            assertThatThrownBy(() -> commentService.updateComment(request))
                .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("댓글 삭제를 요청할 때 ")
    class deleteComment {
        @DisplayName("댓글을 삭제한다.")
        @Test
        void success() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            Comment comment = createComment("content", member, post, null);

            CommentDeleteServiceRequest request =
                new CommentDeleteServiceRequest(comment.getId(), MEMBER_NAME);

            //when
            commentService.deleteComment(request);

            //then
            List<Comment> result = commentRepository.findAll();
            assertThat(result).isEmpty();
        }

        @DisplayName("답글이 달린 댓글을 삭제한다.")
        @Test
        void successWithReply() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            Comment parent = createComment("content", member, post, null);
            Comment comment = createComment("content", member, post, parent);

            CommentDeleteServiceRequest request =
                new CommentDeleteServiceRequest(parent.getId(), MEMBER_NAME);

            //when
            commentService.deleteComment(request);

            //then
            List<Comment> result = commentRepository.findAll();
            assertThat(result).isEmpty();
        }

        @DisplayName("댓글이 존재하지 않으면 댓글을 삭제할 수 없다.")
        @Test
        void failWithNonExistComment() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            CommentDeleteServiceRequest request =
                new CommentDeleteServiceRequest(1L, MEMBER_NAME);

            //when then
            assertThatThrownBy(() -> commentService.deleteComment(request))
                .isInstanceOf(DataNotFoundException.class);
        }

        @DisplayName("유효한 사용자가 아니면 댓글을 삭제할 수 없다.")
        @Test
        void failWithInvalidUser() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title");

            Comment comment = createComment("content", member, post, null);

            CommentDeleteServiceRequest request =
                new CommentDeleteServiceRequest(comment.getId(), "invalidMember");

            //when then
            assertThatThrownBy(() -> commentService.deleteComment(request))
                .isInstanceOf(AccessDeniedException.class);
        }
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

    private Post createPost(Member member, String title) {
        Category category = categoryRepositoryHandler.findByName("string");

        Post post = Post.builder()
            .author(member)
            .title(title)
            .content("content")
            .category(category)
            .build();

        return postRepository.save(post);
    }

    private Comment createComment(String content, Member member, Post post, Comment parent) {
        Comment comment = Comment.builder()
            .content(content)
            .author(member)
            .post(post)
            .parent(parent)
            .build();

        return commentRepository.save(comment);
    }
}
