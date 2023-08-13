package spring.bbs.post.test;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.config.security.SecurityConfig;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.post.PostRequestResponseCreator;
import spring.bbs.profileResolver.CustomActiveProfilesResolver;
import spring.bbs.util.RequestResponseCreator;
import spring.bbs.written.post.controller.PostController;
import spring.bbs.written.post.dto.request.PostListRequest;
import spring.bbs.written.post.dto.request.PostRequest;
import spring.bbs.written.post.dto.response.PostListResponse;
import spring.bbs.written.post.dto.response.PostResponse;
import spring.bbs.written.post.service.PostService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PostController.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class})},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles(resolver = CustomActiveProfilesResolver.class)
public class ControllerTest<T> {
    private final String specificPostUrl = "/api/v1/posts/{id}";
    private final String allPostsUrl = "/api/v1/posts";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;
    @Autowired
    private ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PostRequestResponseCreator requestResponseCreator = new PostRequestResponseCreator();

    private ResultActions requestGetSpecificPost(Long id) throws Exception {
        return mockMvc.perform(get(specificPostUrl, id));
    }

    private ResultActions requestGetPostList() throws Exception {
        return mockMvc.perform(get(allPostsUrl));
    }

    private ResultActions requestWritePost(PostRequest requestBody) throws Exception {
        return mockMvc.perform(post(allPostsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
    }

    private ResultActions requestUpdatePost(Long id, PostRequest requestBody) throws Exception {
        return mockMvc.perform(patch(specificPostUrl, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
    }

    private ResultActions requestDeletePost(Long id) throws Exception {
        return mockMvc.perform(delete(specificPostUrl, id));
    }

    @Nested
    class 게시글_작성 {
        PostRequest validPostRequest;

        public 게시글_작성() throws Exception {
            final RequestResponseCreator<PostRequest> requestResponseCreator1 =
                    new RequestResponseCreator(PostRequest.class, resourceLoader);
            this.validPostRequest = requestResponseCreator1.get("post/CreatePostData.json");
        }

        @Test
        void 유효한게시글이면_성공() throws Exception {
            //given
            PostResponse expect = requestResponseCreator.getCreatePostDataResponse();
            given(postService.createPost(any(PostRequest.class)))
                    .willReturn(expect);
            //when
            ResultActions response = requestWritePost(validPostRequest);
            //then
            response.andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is(expect.getTitle())))
                    .andExpect(jsonPath("$.content", is(expect.getContent())))
                    .andExpect(jsonPath("$.category", is(expect.getCategory())));
            verify(postService).createPost(refEq(validPostRequest));
        }

        @Test
        void 제목없는게시글이면_BadRequest() throws Exception {
            //given
            PostRequest NoTitlePostRequest = new PostRequest();
            NoTitlePostRequest.setTitle("");
            //when
            ResultActions response = requestWritePost(NoTitlePostRequest);
            //then
            response.andExpect(status().isBadRequest());
        }

        @Test
        void 내용없는게시글이면_BadRequest() throws Exception {
            PostRequest NoContentPostRequest = new PostRequest();
            NoContentPostRequest.setContent("");
            //when
            ResultActions response = requestWritePost(NoContentPostRequest);
            //then
            response.andExpect(status().isBadRequest());
        }

        @Test
        void 카테고리없는게시글이면_BadRequest() throws Exception {
            PostRequest NoCategoryPostRequest = new PostRequest();
            NoCategoryPostRequest.setCategory("");
            //when
            ResultActions response = requestWritePost(NoCategoryPostRequest);
            //then
            response.andExpect(status().isBadRequest());
        }
    }

    @Nested
    class 게시글_삭제 {
        @Test
        void 존재하는게시글이면_성공() throws Exception {
            //given
            Long id = 1L;
            doNothing().when(postService).deletePost(id);
            //when
            ResultActions result = requestDeletePost(id);
            //then
            result.andExpect(status().isOk());
            verify(postService).deletePost(id);
        }
    }

    @Nested
    class 게시글_수정 {

        PostRequest validPostUpdateRequest;

        public 게시글_수정() throws Exception {
            final RequestResponseCreator<PostRequest> requestResponseCreator1 =
                    new RequestResponseCreator(PostRequest.class, resourceLoader);
            this.validPostUpdateRequest = requestResponseCreator1.get("post/CreatePostData.json");
        }

        @Test
        void 존재하는게시글이고_유효한게시글내용이면_성공() throws Exception {
            //given
            PostResponse expect = requestResponseCreator.getUpdatePostDataResponse();
            Long id = 1L;
            given(postService.updatePost(any(PostRequest.class), any(Long.class))).willReturn(expect);
            //when
            ResultActions result = requestUpdatePost(id, validPostUpdateRequest);
            //then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is(expect.getTitle())))
                    .andExpect(jsonPath("$.content", is(expect.getContent())))
                    .andExpect(jsonPath("$.category", is(expect.getCategory())));
            verify(postService).updatePost(refEq(validPostUpdateRequest), anyLong());
        }
    }

    @Nested
    class 게시글_조회 {
        @Test
        void 존재하는게시글이면_성공() throws Exception {
            //given
            PostResponse expect = requestResponseCreator.getCreatePostDataResponse();
            Long id = 1L;
            given(postService.getPost(any(Long.class))).willReturn(expect);
            //when
            ResultActions result = requestGetSpecificPost(id);
            //then
            result.andDo(print()).
                    andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is(expect.getTitle())))
                    .andExpect(jsonPath("$.content", is(expect.getContent())))
                    .andExpect(jsonPath("$.category", is(expect.getCategory())));
        }

        @Test
        void 존재하지않는게시글이면_NotFound() throws Exception {
            //given
            Long id = 1L;
            given(postService.getPost(any(Long.class))).willThrow(DataNotFoundException.class);
            //when
            ResultActions result = requestGetSpecificPost(id);
            //then
            result.andExpect(status().isNotFound());
        }
    }

    @Nested
    class 게시글_목록_조회 {

        private PostListRequest req;

        public 게시글_목록_조회() throws Exception {
//            final RequestResponseCreator<PostListRequest> requestResponseCreator1 =
//                    new RequestResponseCreator(PostListRequest.class, resourceLoader);
//            req = requestResponseCreator1.get("post/PostListResponseData.json");
        }

        @Test
        void 성공() throws Exception {
            //given
            PostListRequest req = requestResponseCreator.getPostListDataRequest();
            Page<PostListResponse> response = requestResponseCreator.getPostListDataResponse();
            List<PostListResponse> expect = response.toList();
            given(postService.getPostList(any(PostListRequest.class))).willReturn(response);
            //when
            ResultActions result = requestGetPostList();
            //then
            result.andDo(print()).
                    andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.[0].title", is(expect.get(0).getTitle())))
                    .andExpect(jsonPath("$.content.[0].authorResponse.name",
                            is(expect.get(0).getAuthorResponse().getName())))
                    .andExpect(jsonPath("$.content.[1].title", is(expect.get(1).getTitle())))
                    .andExpect(jsonPath("$.content.[1].authorResponse.name",
                            is(expect.get(1).getAuthorResponse().getName())))
                    .andExpect(jsonPath("$.content.[2].title", is(expect.get(2).getTitle())))
                    .andExpect(jsonPath("$.content.[2].authorResponse.name",
                            is(expect.get(2).getAuthorResponse().getName())));
            verify(postService).getPostList(refEq(req));
        }
    }
}
