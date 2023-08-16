package spring.bbs.post.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.config.security.SecurityConfig;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.member.dto.response.MemberResponse;
import spring.bbs.post.controller.PostController;
import spring.bbs.post.dto.request.PostListRequest;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.dto.response.PostListResponse;
import spring.bbs.post.dto.response.PostResponse;
import spring.bbs.post.service.PostService;
import spring.profileResolver.CustomActiveProfilesResolver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PostController.class},
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class})},
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles(resolver = CustomActiveProfilesResolver.class)
public class PostControllerTest {
    private final String specificPostUrl = "/api/v1/posts/{id}";
    private final String allPostsUrl = "/api/v1/posts";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class GetPost {

        private ResultActions request(Long id) throws Exception {
            return mockMvc.perform(get(specificPostUrl, id));
        }

        @Test
        @DisplayName("게시글을 조회할 수 있다.")
        void getPost() throws Exception {
            //given
            Long id = 1L;
            PostResponse expect = PostResponse.builder()
                .title("createTestTitle")
                .content("createTestContent")
                .category("string")
                .build();
            given(postService.getPost(any(Long.class))).willReturn(expect);
            //when
            ResultActions result = request(id);
            //then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(expect.getTitle())))
                .andExpect(jsonPath("$.content", is(expect.getContent())))
                .andExpect(jsonPath("$.category", is(expect.getCategory())));
        }

        @Test
        @DisplayName("게시글이 없다면 조회할 수 없다.")
        void getNonExistedPost() throws Exception {
            //given
            Long id = 1L;
            given(postService.getPost(any(Long.class))).willThrow(DataNotFoundException.class);
            //when
            ResultActions result = request(id);
            //then
            result.andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetPostList {

        private ResultActions request() throws Exception {
            return mockMvc.perform(get(allPostsUrl));
        }

        @Test
        @DisplayName("게시글 목록을 조회할 수 있다.")
        void getPostList() throws Exception {
            //given
            PostListRequest req = new PostListRequest(1, "string", null, null);
            Page<PostListResponse> response = getPostListResponse(LocalDateTime.now());
            List<PostListResponse> expect = response.toList();
            given(postService.getPostList(any(PostListRequest.class))).willReturn(response);
            //when
            ResultActions result = request();
            //then
            result.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].title", is(expect.get(0).getTitle())))
                .andExpect(jsonPath("$.content.[0].author.name",
                    is(expect.get(0).getAuthor().getName())))
                .andExpect(jsonPath("$.content.[1].title", is(expect.get(1).getTitle())))
                .andExpect(jsonPath("$.content.[1].author.name",
                    is(expect.get(1).getAuthor().getName())))
                .andExpect(jsonPath("$.content.[2].title", is(expect.get(2).getTitle())))
                .andExpect(jsonPath("$.content.[2].author.name",
                    is(expect.get(2).getAuthor().getName())));
            verify(postService).getPostList(refEq(req));
        }
    }

    @Nested
    class CreatePost {

        private ResultActions request(PostRequest requestBody) throws Exception {
            return mockMvc.perform(post(allPostsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
        }

        @Test
        @DisplayName("게시글을 작성하면 작성된 게시글 정보를 반환한다.")
        void createPost() throws Exception {
            //given
            PostRequest req = PostRequest.builder()
                .title("createTestTitle")
                .content("createTestContent")
                .category("string")
                .build();
            PostResponse expect = PostResponse.builder()
                .title("createTestTitle")
                .content("createTestContent")
                .category("string")
                .build();
            given(postService.createPost(any(PostRequest.class)))
                .willReturn(expect);
            //when //then
            request(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(req.getTitle())))
                .andExpect(jsonPath("$.content", is(req.getContent())))
                .andExpect(jsonPath("$.category", is(req.getCategory())));
            verify(postService).createPost(refEq(req));
        }

        @Test
        @DisplayName("게시글 작성 시 제목은 필수값이다.")
        void createPostWithoutTitle() throws Exception {
            //given
            PostRequest NoTitlePostRequest = PostRequest.builder()
                .content("createTestContent")
                .category("string")
                .build();
            //when //then
            request(NoTitlePostRequest)
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("게시글 작성 시 내용은 필수값이다.")
        void createPostWithoutContent() throws Exception {
            //given
            PostRequest NoContentPostRequest = PostRequest.builder()
                .title("createTestTitle")
                .category("string")
                .build();
            //when //then
            request(NoContentPostRequest)
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("게시글 작성 시 카테고리는 필수값이다.")
        void createPostWithoutCategory() throws Exception {
            PostRequest NoCategoryPostRequest = PostRequest.builder()
                .title("createTestTitle")
                .content("createTestContent")
                .build();
            //when
            ResultActions response = request(NoCategoryPostRequest);
            //then
            response.andExpect(status().isBadRequest());
        }
    }

    @Nested
    class DeletePost {
        private ResultActions request(Long id) throws Exception {
            return mockMvc.perform(delete(specificPostUrl, id));
        }

        @Test
        @DisplayName("게시글을 삭제할 수 있다.")
        void deletePost() throws Exception {
            //given
            long id = 1L;
            doNothing().when(postService).deletePost(id);
            //when //then
            request(id)
                .andExpect(status().isOk());
            verify(postService).deletePost(id);
        }

        @Test
        @DisplayName("게시글이 없다면 삭제할 수 없다.")
        void deleteNonExistedPost() throws Exception {
            //given
            long id = 1L;
            doThrow(new DataNotFoundException("게시글이 존재하지 않습니다.")).when(postService).deletePost(id);

            //when //then
            request(id)
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class ModifyPost {

        private ResultActions request(Long id, PostRequest requestBody) throws Exception {
            return mockMvc.perform(patch(specificPostUrl, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
        }


        @Test
        @DisplayName("게시글을 수정할 수 있다.")
        void modifyPost() throws Exception {
            //given
            PostRequest req = PostRequest.builder()
                .title("updateTestTitle")
                .content("updateTestContent")
                .category("string")
                .build();
            PostResponse expect = PostResponse.builder()
                .title("updateTestTitle")
                .content("updateTestContent")
                .category("string")
                .build();
            Long id = 1L;
            given(postService.updatePost(any(PostRequest.class), any(Long.class))).willReturn(expect);
            //when
            ResultActions result = request(id, req);
            //then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(req.getTitle())))
                .andExpect(jsonPath("$.content", is(req.getContent())))
                .andExpect(jsonPath("$.category", is(req.getCategory())));
            verify(postService).updatePost(refEq(req), anyLong());
        }

        @Test
        @DisplayName("게시글이 없다면 수정할 수 없다.")
        void modifyNonExistedPost() throws Exception {
            //given
            PostRequest req = PostRequest.builder()
                .title("updateTestTitle")
                .content("updateTestContent")
                .category("string")
                .build();
            Long id = 1L;
            given(postService.updatePost(any(PostRequest.class), any(Long.class)))
                .willThrow(DataNotFoundException.class);
            //when //then
            request(id, req)
                .andExpect(status().isNotFound());
        }
    }

    private Page<PostListResponse> getPostListResponse(LocalDateTime dateTime) {
        int num = 3;
        List<PostListResponse> postList = new ArrayList<>();

        MemberResponse memberResponse = MemberResponse.create(1L, "testMember");
        for (int i = 1; i <= 1 + num; i++) {
            postList.add(PostListResponse.create((long) i, "TestTitle" + i, dateTime, memberResponse));
        }
        PageRequest pageRequest = PageRequest.of(0, 10);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), postList.size());
        return new PageImpl<>(postList.subList(start, end), pageRequest, postList.size());
    }
}
