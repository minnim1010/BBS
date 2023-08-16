package spring.bbs.comment.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import spring.bbs.AuthenticationTests;
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.dto.request.CommentCreateRequest;
import spring.bbs.comment.dto.request.CommentListRequest;
import spring.bbs.comment.dto.request.CommentUpdateRequest;
import spring.bbs.comment.dto.service.CommentDeleteServiceRequest;
import spring.bbs.comment.repository.CommentRepository;
import spring.bbs.comment.service.CommentService;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.repository.PostRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentIntegrationTest extends AuthenticationTests {

    private static final String TEST_COMMENT_CONTENT = "testComment";
    private final String username = "CommentTestUser1";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepositoryHandler categoryRepositoryHandler;
    @Autowired
    private CommentService commentService;

    public CommentIntegrationTest() {
        setMemberName(username);
    }

    @AfterEach
    void deleteComment() {
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    class GetCommentList {

        private static final String url = "/api/v1/comments";

        @Test
        @DisplayName("누구나 게시글의 댓글 목록을 조회할 수 있다.")
        void getCommentList() throws Exception {
            // given
            Member member = createMember(username);
            Post post = createPost(member);
            Comment comment1 = createComment(post, member, "comment1");
            Comment comment2 = createComment(post, member, "comment2");
            Comment comment3 = createComment(post, member, "comment3");
            commentRepository.saveAll(List.of(comment1, comment2, comment3));
            CommentListRequest req = new CommentListRequest(1, "", post.getId());
            // when // then
            mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", String.valueOf(req.getPage()))
                    .param("postId", String.valueOf(req.getPostId()))
                    .param("searchKeyword", req.getSearchKeyword()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].content", is(comment1.getContent())))
                .andExpect(jsonPath("$.content.[1].content", is(comment2.getContent())))
                .andExpect(jsonPath("$.content.[2].content", is(comment3.getContent())));
            List<Comment> comments = commentRepository.findAll();
            assertThat(comments).hasSize(3)
                .extracting("content")
                .containsExactlyInAnyOrder(
                    "comment1", "comment2", "comment3");
        }

        @Test
        @DisplayName("게시글 댓글 목록 조회 시 대댓글과 댓글이 올바른 순서대로 조회된다.")
        void getCommentListWithCorrectOrder() throws Exception {
            // given
            Member member = createMember(username);
            Post post = createPost(member);
            Comment parentComment = createComment(post, member, "parentComment");
            Comment childComment1 = createComment(post, member, "childComment1", parentComment, 1);
            Comment grandChildComment = createComment(post, member, "grandChildComment1", childComment1, 2);
            Comment childComment2 = createComment(post, member, "childComment2", parentComment, 3);
            commentRepository.saveAll(List.of(parentComment, childComment2, childComment1, grandChildComment));
            CommentListRequest req = new CommentListRequest(1, "", post.getId());
            // when // then
            mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", String.valueOf(req.getPage()))
                    .param("postId", String.valueOf(req.getPostId()))
                    .param("searchKeyword", req.getSearchKeyword()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].content", is(parentComment.getContent())))
                .andExpect(jsonPath("$.content.[1].content", is(childComment1.getContent())))
                .andExpect(jsonPath("$.content.[2].content", is(grandChildComment.getContent())))
                .andExpect(jsonPath("$.content.[3].content", is(childComment2.getContent())));
            List<Comment> comments = commentRepository.findAll();
            assertThat(comments).hasSize(4)
                .extracting("content", "groupOrder")
                .containsExactlyInAnyOrder(
                    Tuple.tuple(parentComment.getContent(), parentComment.getGroupOrder()),
                    Tuple.tuple(childComment1.getContent(), childComment1.getGroupOrder()),
                    Tuple.tuple(grandChildComment.getContent(), grandChildComment.getGroupOrder()),
                    Tuple.tuple(childComment2.getContent(), childComment2.getGroupOrder()));
        }
    }

    @Nested
    class CreateComment {
        private static final String url = "/api/v1/comments";

        @Test
        @DisplayName("회원은 게시글에 댓글을 달 수 있다.")
        void createCommentAtPost() throws Exception {
            // given
            Member member = createMember(username);
            Post post = createPost(member);
            CommentCreateRequest req =
                new CommentCreateRequest(TEST_COMMENT_CONTENT, post.getId(), null);
            String tokenHeader = getJwtTokenHeader(getJwtToken());
            // when //then
            mockMvc.perform(
                    post(url)
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is(req.getContent())));
            List<Comment> comments = commentRepository.findAll();
            assertThat(comments).hasSize(1)
                .extracting("content")
                .contains(TEST_COMMENT_CONTENT);
        }

        @Test
        @DisplayName("회원은 댓글에 대댓글을 달 수 있다.")
        void createCommentWithParentComment() throws Exception {
            // given
            Member member = createMember(username);
            Post post = createPost(member);
            Comment comment = createComment(post, member, "parentComment");
            CommentCreateRequest req =
                new CommentCreateRequest(TEST_COMMENT_CONTENT, post.getId(), comment.getId());
            String tokenHeader = getJwtTokenHeader(getJwtToken());
            // when //then
            mockMvc.perform(
                    post(url)
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is(req.getContent())));
            List<Comment> comments = commentRepository.findAll();
            assertThat(comments).hasSize(2)
                .extracting("content", "groupOrder")
                .containsExactlyInAnyOrder(
                    Tuple.tuple(TEST_COMMENT_CONTENT, 1),
                    Tuple.tuple("parentComment", 0));
        }

        @Test
        @DisplayName("회원이 예전 댓글에 대댓글을 달면, 다른 대댓글보다 최근에 작성되었더라도 대댓글 순서가 높다.")
        void createCommentWithOldParentComment() throws Exception {
            // given
            Member member = createMember(username);
            Post post = createPost(member);
            Comment parentComment = createComment(post, member, "parentComment");
            Comment childComment1 = createComment(post, member, "childComment1", parentComment, 1);
            Comment grandChildComment = createComment(post, member, "grandChildComment1", childComment1, 2);
            Comment childComment2 = createComment(post, member, "childComment2", parentComment, 3);
            commentRepository.saveAll(List.of(parentComment, childComment2, childComment1, grandChildComment));
            CommentCreateRequest req =
                new CommentCreateRequest("newComment", post.getId(), childComment1.getId());
            String tokenHeader = getJwtTokenHeader(getJwtToken());
            // when //then
            mockMvc.perform(
                    post(url)
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is(req.getContent())));
            List<Comment> comments = commentRepository.findAll();
            assertThat(comments).hasSize(5)
                .extracting("content", "groupOrder")
                .containsExactlyInAnyOrder(
                    Tuple.tuple("parentComment", 0),
                    Tuple.tuple("childComment1", 1),
                    Tuple.tuple("grandChildComment1", 2),
                    Tuple.tuple("newComment", 3),
                    Tuple.tuple("childComment2", 4));
        }
    }

    @Nested
    class ModifyComment {
        private static final String url = "/api/v1/comments/{id}";

        @Test
        @DisplayName("댓글 작성자는 게시글의 댓글을 수정할 수 있다.")
        void modifyComment() throws Exception {
            // given
            Member member = createMember(username);
            Post post = createPost(member);
            Comment comment = createComment(post, member);
            String updateContent = "UpdateComment";
            CommentUpdateRequest req = new CommentUpdateRequest(updateContent);
            String tokenHeader = getJwtTokenHeader(getJwtToken());
            // when // then
            mockMvc.perform(
                    patch(url, comment.getId())
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is(req.getContent())));
            List<Comment> posts = commentRepository.findAll();
            assertThat(posts).hasSize(1)
                .extracting("content")
                .contains(updateContent);
        }
    }

    @Nested
    class DeleteComment {
        private static final String url = "/api/v1/comments/{id}";

        @Test
        @DisplayName("댓글 작성자는 대댓글이 없는 게시글의 댓글을 삭제할 수 있다.")
        void deleteComment() throws Exception {
            // given
            Member member = createMember(username);
            Post post = createPost(member);
            Comment comment = Comment.of("content", member, post);
            commentRepository.saveAll(List.of(comment));
            String tokenHeader = getJwtTokenHeader(getJwtToken());
            // when // then
            mockMvc.perform(
                    delete(url, comment.getId())
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
            assertThat(commentRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("댓글에 자식 대댓글이 있다면 실제 삭제되지 않고, 삭제 처리만 수행한다.")
        void deleteParentCommentWithChildComment() throws Exception {
            // given
            Member member = createMember(username);
            Post post = createPost(member);
            Comment parentComment = createComment(post, member, "parentComment");
            Comment childComment = createComment(post, member, "childComment", parentComment, 1);
            commentRepository.saveAll(List.of(parentComment, childComment));
            String tokenHeader = getJwtTokenHeader(getJwtToken());
            // when // then
            mockMvc.perform(
                    delete(url, parentComment.getId())
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
            List<Comment> result = commentRepository.findAll();
            assertThat(result).hasSize(2)
                .extracting("content", "isDeleted", "canDeleted")
                .containsExactlyInAnyOrder(
                    Tuple.tuple("이미 삭제된 댓글입니다.", true, false),
                    Tuple.tuple(childComment.getContent(), false, true)
                );
        }

        @Test
        @DisplayName("자식 대댓글이 모두 삭제된 부모 댓글 삭제 시 부모 댓글과 자식 대댓글 모두 실제 삭제된다.")
        void deleteParentCommentWithAllDeletedChildComment() throws Exception {
            // given
            Member member = createMember(username);
            Post post = createPost(member);
            Comment parentComment = createComment(post, member, "parentComment");
            Comment childComment = createComment(post, member, "childComment", parentComment, 1);
            commentService.deleteComment(CommentDeleteServiceRequest.of(childComment.getId(), member.getName()));
            String tokenHeader = getJwtTokenHeader(getJwtToken());
            // when // then
            mockMvc.perform(
                    delete(url, parentComment.getId())
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
            List<Comment> result = commentRepository.findAll();
            assertThat(result).hasSize(0);
        }
    }

    private Post createPost(Member author) {
        PostRequest req = new PostRequest(
            "createTestTitle", "createTestContent", "string");
        Post post = Post.of(req, categoryRepositoryHandler.findByName(req.getCategory()), author);
        return postRepository.save(post);
    }

    private Comment createComment(Post post, Member author, String content, Comment parentComment, int order) {
        Comment comment = Comment.of(content, author, post, parentComment, order);
        return commentRepository.save(comment);
    }

    private Comment createComment(Post post, Member author, String content) {
        Comment comment = Comment.of(content, author, post);
        return commentRepository.saveAndFlush(comment);
    }

    private Comment createComment(Post post, Member author) {
        Comment comment = Comment.of("commentContent", author, post);
        return commentRepository.saveAndFlush(comment);
    }
}
