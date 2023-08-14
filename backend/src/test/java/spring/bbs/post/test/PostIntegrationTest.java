package spring.bbs.post.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.AuthenticationTests;
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostIntegrationTest extends AuthenticationTests {

    private final String username = "postTestUser1";
    private final String otherUsername = "postTestUser2";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepositoryHandler categoryRepositoryHandler;

    public PostIntegrationTest() {
        setMemberName(username);
    }

    @AfterEach
    void deletePost() {
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    class GetPost {
        private static final String url = "/api/v1/posts/{id}";

        @Test
        @DisplayName("누구나 게시글을 조회할 수 있다.")
        void givenExistedPost_thenGetPost() throws Exception {
            //given
            Post post = createPost(createMember(username));
            //when //then
            mockMvc.perform(get(url, post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(post.getTitle())))
                .andExpect(jsonPath("$.content", is(post.getContent())))
                .andExpect(jsonPath("$.category", is(post.getCategory().getName())));
            List<Post> posts = postRepository.findAll();
            assertThat(posts).hasSize(1)
                .extracting("title", "content")
                .contains(Tuple.tuple(post.getTitle(), post.getContent()));
            assertThat(posts.get(0).getCategory().getName()).isEqualTo(post.getCategory().getName());
        }

        @Test
        @DisplayName("게시글이 없다면 조회할 수 없다.")
        void givenNonExistedPost_thenDataNotFoundError() throws Exception {
            //given
            Post post = createPost(createMember(username));
            postRepository.delete(post);
            //when // then
            mockMvc.perform(get(url, post.getId()))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetPostList {
        private static final String url = "/api/v1/posts";

        @Test
        @DisplayName("누구나 게시글 목록을 조회할 수 있다.")
        void givenExistedPosts_thenGetPostList() throws Exception {
            //given
            List<Post> postList = createPostList();
            //when
            ResultActions response = mockMvc.perform(get(url));

            response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].title", is(postList.get(2).getTitle())))
                .andExpect(jsonPath("$.content.[0].author.name", is(postList.get(2).getAuthor().getName())))
                .andExpect(jsonPath("$.content.[1].title", is(postList.get(1).getTitle())))
                .andExpect(jsonPath("$.content.[1].author.name", is(postList.get(1).getAuthor().getName())))
                .andExpect(jsonPath("$.content.[2].title", is(postList.get(0).getTitle())))
                .andExpect(jsonPath("$.content.[2].author.name", is(postList.get(0).getAuthor().getName())));
            List<Post> posts = postRepository.findAll();
            assertThat(posts).hasSize(3)
                .extracting("title", "content")
                .containsExactlyInAnyOrder(
                    Tuple.tuple(postList.get(0).getTitle(), postList.get(0).getContent()),
                    Tuple.tuple(postList.get(1).getTitle(), postList.get(1).getContent()),
                    Tuple.tuple(postList.get(2).getTitle(), postList.get(2).getContent())
                );
        }
    }

    @Nested
    class CreatePost {

        private static final String url = "/api/v1/posts";

        @Test
        @DisplayName("회원은 게시글을 작성할 수 있다.")
        void givenNewPost_thenGetNewPost() throws Exception {
            //given
            PostRequest req = new PostRequest(
                "createTestTitle", "createTestContent", "string");
            String tokenHeader = getJwtTokenHeader(getJwtToken());
            //when //then
            mockMvc.perform(
                    post(url)
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(req.getTitle())))
                .andExpect(jsonPath("$.content", is(req.getContent())))
                .andExpect(jsonPath("$.category", is(req.getCategory())));
            List<Post> posts = postRepository.findAll();
            assertThat(posts).hasSize(1)
                .extracting("title", "content")
                .contains(Tuple.tuple(req.getTitle(), req.getContent()));
        }
    }

    @Nested
    class ModifyPost {

        private static final String url = "/api/v1/posts/{id}";

        @Test
        @DisplayName("작성자는 게시글을 수정힐 수 있다.")
        void givenExistedPost_thenGetUpdatedPost() throws Exception {
            //given
            Long postId = createPost(createMember(username)).getId();
            PostRequest req = new PostRequest(
                "upDateTestTitle", "upDateTestContent", "string");
            String tokenHeader = getJwtTokenHeader(getJwtToken());
            //when //then
            mockMvc.perform(
                    patch(url, postId)
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(req.getTitle())))
                .andExpect(jsonPath("$.content", is(req.getContent())))
                .andExpect(jsonPath("$.category", is(req.getCategory())));
            List<Post> posts = postRepository.findAll();
            assertThat(posts).hasSize(1)
                .extracting("title", "content")
                .contains(Tuple.tuple(req.getTitle(), req.getContent()));
        }

        @Test
        @DisplayName("게시글이 없다면 게시글을 수정할 수 없다.")
        void givenNonExistedPost_whenUpdate_thenDataNotFoundError() throws Exception {
            //given
            Post post = createPost(createMember(username));
            postRepository.delete(post);
            String tokenHeader = getJwtTokenHeader(getJwtToken());
            PostRequest req = new PostRequest(
                "upDateTestTitle", "upDateTestContent", "string");
            //when //then
            mockMvc.perform(
                    patch(url, post.getId())
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("작성자가 아니면 게시글을 수정할 수 없다.")
        void givenExistedPost_AndOtherUser_whenUpdate_thenForbiddenError() throws Exception {
            //given
            Long postId = createPost(createMember(username)).getId();
            String tokenHeader = getJwtTokenHeader(getJwtToken(otherUsername));
            PostRequest req = new PostRequest(
                "upDateTestTitle", "upDateTestContent", "string");
            //when //then
            ResultActions response = mockMvc.perform(patch(url, postId)
                    .header(AUTHENTICATION_HEADER, tokenHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    class DeletePost {
        private static final String url = "/api/v1/posts/{id}";

        @Test
        @DisplayName("작성자는 게시글을 삭제할 수 있다.")
        void givenExistedPost_thenPostDelete() throws Exception {
            //given
            Long postId = createPost(createMember(username)).getId();
            String token = getJwtToken();
            String tokenHeader = getJwtTokenHeader(token);
            //when //then
            mockMvc.perform(
                    delete(url, postId)
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                )
                .andExpect(status().isOk());
            assertThat(postRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("게시글이 없다면 게시글을 삭제할 수 없다.")
        void givenNonExistedPost_whenDelete_thenDataNotFoundError() throws Exception {
            //given
            Post post = createPost(createMember(username));
            postRepository.delete(post);

            String token = getJwtToken();
            String tokenHeader = getJwtTokenHeader(token);
            //when
            mockMvc.perform(
                    delete(url, post.getId())
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("작성자가 아니면 게시글을 삭제할 수 없다.")
        void givenExistedPost_AndOtherUser_whenDelete_thenForbiddenError() throws Exception {
            //given
            Long postId = createPost(createMember(username)).getId();
            String token = getJwtToken(otherUsername);
            String tokenHeader = getJwtTokenHeader(token);
            //when //then
            mockMvc.perform(
                    delete(url, postId)
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
        }
    }

    private Post createPost(Member author) {
        PostRequest req = new PostRequest(
            "createTestTitle", "createTestContent", "string");
        Post post = Post.of(req, categoryRepositoryHandler.findByName(req.getCategory()), author);
        return postRepository.save(post);
    }

    private List<Post> createPostList() {
        List<Post> postList = new ArrayList<>();
        Member author = getMember(username);
        for (int i = 0; i < 3; i++) {
            PostRequest postRequest = new PostRequest("TestTitle" + i, "TestContent" + i, "string");
            postList.add(Post.of(postRequest, categoryRepositoryHandler.findByName(postRequest.getCategory()), author));
        }

        return postRepository.saveAll(postList);
    }

    private Member getMember(String name) {
        return memberRepository.findByName(name)
            .orElseGet(() -> createMember(name));
    }

}
