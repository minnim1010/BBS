package spring.bbs.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.common.jwt.JwtProvider;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.repository.PostRepository;
import spring.helper.AccessTokenProvider;
import spring.helper.MemberCreator;
import spring.helper.PostCreator;
import spring.profileResolver.ProfileConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ProfileConfiguration
public class PostIntegrationTest {

    private static final String MEMBER_NAME = "PostTestUser";
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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CategoryRepositoryHandler categoryRepositoryHandler;
    @Autowired
    private JwtProvider jwtProvider;

    private AccessTokenProvider accessTokenProvider;
    private MemberCreator memberCreator;
    private PostCreator postCreator;

    @AfterEach
    void deletePost() {
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @PostConstruct
    void init() {
        accessTokenProvider = new AccessTokenProvider(jwtProvider, MEMBER_NAME);
        memberCreator = new MemberCreator(memberRepository);
        postCreator = new PostCreator(postRepository, categoryRepositoryHandler);
    }

    @Nested
    class GetPost {
        private static final String url = "/api/v1/posts/{id}";

        @Test
        @DisplayName("누구나 게시글을 조회할 수 있다.")
        void givenExistedPost_thenGetPost() throws Exception {
            //given
            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
            Post post = postCreator.createPost(member);
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
            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
            Post post = postCreator.createPost(member);
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
            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
            Post post1 = postCreator.createPost(member, "createTitle1", "createContent1");
            Post post2 = postCreator.createPost(member, "createTitle2", "createContent2");
            Post post3 = postCreator.createPost(member, "createTitle3", "createContent3");
            //when
            ResultActions response = mockMvc.perform(get(url));

            response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].title", is(post3.getTitle())))
                .andExpect(jsonPath("$.content.[0].author.name", is(post3.getAuthor().getName())))
                .andExpect(jsonPath("$.content.[1].title", is(post2.getTitle())))
                .andExpect(jsonPath("$.content.[1].author.name", is(post2.getAuthor().getName())))
                .andExpect(jsonPath("$.content.[2].title", is(post1.getTitle())))
                .andExpect(jsonPath("$.content.[2].author.name", is(post1.getAuthor().getName())));
            List<Post> posts = postRepository.findAll();
            assertThat(posts).hasSize(3)
                .extracting("title", "content")
                .containsExactlyInAnyOrder(
                    Tuple.tuple(post1.getTitle(), post1.getContent()),
                    Tuple.tuple(post2.getTitle(), post2.getContent()),
                    Tuple.tuple(post3.getTitle(), post3.getContent())
                );
        }
    }

    @Nested
    class createPost {

        private static final String url = "/api/v1/posts";

        @Test
        @DisplayName("회원은 게시글을 작성할 수 있다.")
        void givenNewPost_thenGetNewPost() throws Exception {
            //given
            memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
            PostRequest req = new PostRequest(
                "createTestTitle", "createTestContent", "string");
            String tokenHeader = accessTokenProvider.getUserRoleTokenWithHeaderPrefix();
            //when //then
            mockMvc.perform(
                    post(url)
                        .header(accessTokenProvider.AUTHENTICATION_HEADER, tokenHeader)
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
            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
            Long postId = postCreator.createPost(member).getId();
            PostRequest req = new PostRequest(
                "upDateTestTitle", "upDateTestContent", "string");
            String tokenHeader = accessTokenProvider.getUserRoleTokenWithHeaderPrefix();
            //when //then
            mockMvc.perform(
                    patch(url, postId)
                        .header(accessTokenProvider.AUTHENTICATION_HEADER, tokenHeader)
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
            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
            Post post = postCreator.createPost(member);
            postRepository.delete(post);
            String tokenHeader = accessTokenProvider.getUserRoleTokenWithHeaderPrefix();
            PostRequest req = new PostRequest(
                "upDateTestTitle", "upDateTestContent", "string");
            //when //then
            mockMvc.perform(
                    patch(url, post.getId())
                        .header(accessTokenProvider.AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("작성자가 아니면 게시글을 수정할 수 없다.")
        void givenExistedPost_AndOtherUser_whenUpdate_thenForbiddenError() throws Exception {
            //given
            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
            Long postId = postCreator.createPost(member).getId();
            String tokenHeader = accessTokenProvider.getUserRoleTokenWithHeaderPrefix(accessTokenProvider.createAccessToken(otherUsername));
            PostRequest req = new PostRequest(
                "upDateTestTitle", "upDateTestContent", "string");
            //when //then
            mockMvc.perform(patch(url, postId)
                    .header(accessTokenProvider.AUTHENTICATION_HEADER, tokenHeader)
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
            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
            Long postId = postCreator.createPost(member).getId();
            String tokenHeader = accessTokenProvider.getUserRoleTokenWithHeaderPrefix();
            //when //then
            mockMvc.perform(
                    delete(url, postId)
                        .header(accessTokenProvider.AUTHENTICATION_HEADER, tokenHeader)
                )
                .andExpect(status().isOk());
            assertThat(postRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("게시글이 없다면 게시글을 삭제할 수 없다.")
        void givenNonExistedPost_whenDelete_thenDataNotFoundError() throws Exception {
            //given
            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
            Post post = postCreator.createPost(member);
            postRepository.delete(post);

            String token = accessTokenProvider.createAccessToken();
            String tokenHeader = accessTokenProvider.getUserRoleTokenWithHeaderPrefix(token);
            //when
            mockMvc.perform(
                    delete(url, post.getId())
                        .header(accessTokenProvider.AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("작성자가 아니면 게시글을 삭제할 수 없다.")
        void givenExistedPost_AndOtherUser_whenDelete_thenForbiddenError() throws Exception {
            //given
            Member member = memberCreator.createMember(MEMBER_NAME, passwordEncoder.encode(MEMBER_NAME));
            Long postId = postCreator.createPost(member).getId();
            String token = accessTokenProvider.createAccessToken(otherUsername);
            String tokenHeader = accessTokenProvider.getUserRoleTokenWithHeaderPrefix(token);
            //when //then
            mockMvc.perform(
                    delete(url, postId)
                        .header(accessTokenProvider.AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
        }
    }
}
