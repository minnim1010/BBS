package spring.bbs.comment.test;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import spring.bbs.comment.repository.CommentRepository;
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
            Comment comment1 = createComment(post, member);
            Comment comment2 = createComment(post, member);
            Comment comment3 = createComment(post, member);
            commentRepository.saveAll(List.of(comment1, comment2, comment3));
            CommentListRequest req = new CommentListRequest(1, "", post.getId());
            // when // then
            mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", String.valueOf(req.getPage()))
                    .param("postId", String.valueOf(req.getPostId()))
                    .param("searchKeyword", req.getSearchKeyword()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content", is(comment3.getContent())))
                .andExpect(jsonPath("$[1].content", is(comment2.getContent())))
                .andExpect(jsonPath("$[2].content", is(comment1.getContent())));
            List<Comment> comments = commentRepository.findAll();
            assertThat(comments).hasSize(3)
                .extracting("content")
                .containsExactlyInAnyOrder(
                    TEST_COMMENT_CONTENT, TEST_COMMENT_CONTENT, TEST_COMMENT_CONTENT);
        }
    }

    @Nested
    class CreateComment {
        private static final String url = "/api/v1/comments";

        @Test
        @DisplayName("회원은 게시글에 댓글을 달 수 있다.")
        void createComment() throws Exception {
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
            List<Comment> posts = commentRepository.findAll();
            assertThat(posts).hasSize(1)
                .extracting("content")
                .contains(TEST_COMMENT_CONTENT, TEST_COMMENT_CONTENT, TEST_COMMENT_CONTENT);
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
        @DisplayName("댓글 작성자는 게시글의 댓글을 삭제할 수 있다.")
        void deleteComment() throws Exception {
            // given
            Member member = createMember(username);
            Post post = createPost(member);
            Comment comment = createComment(post, member);
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
    }

    private Post createPost(Member author) {
        PostRequest req = new PostRequest(
            "createTestTitle", "createTestContent", "string");
        Post post = Post.of(req, categoryRepositoryHandler.findByName(req.getCategory()), author);
        return postRepository.save(post);
    }

    private Comment createComment(Post post, Member author) {
        Comment comment = Comment.of(TEST_COMMENT_CONTENT, author, post, null);
        return commentRepository.save(comment);
    }
}
