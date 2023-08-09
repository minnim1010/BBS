package spring.bbs.post;


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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.config.security.SecurityConfig;
import spring.bbs.written.post.controller.PostController;
import spring.bbs.written.post.dto.request.MediaPostRequest;
import spring.bbs.written.post.dto.request.PostListRequest;
import spring.bbs.written.post.dto.request.PostRequest;
import spring.bbs.written.post.dto.response.MediaPostResponse;
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
public class ControllerTest extends PostRequestCreator {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;

    @Test
    void 게시글_작성_성공() throws Exception{
        //given
        MediaPostRequest req = getCreatePostDataRequest();
        MediaPostResponse expect = getCreatePostDataResponse();
        given(postService.createPost(any(MediaPostRequest.class)))
                .willReturn(expect);
        //when then
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(expect.getTitle())))
                .andExpect(jsonPath("$.content", is(expect.getContent())))
                .andExpect(jsonPath("$.category", is(expect.getCategory())));
        verify(postService).createPost(refEq(req));
    }

    @Test
    void 제목없는게시글_작성_실패_BadRequest에러() throws Exception{
        //given
        MediaPostRequest req = getCreatePostDataRequest();
        req.setTitle("");
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 내용없는게시글_작성_실패_BadRequest() throws Exception{
        MediaPostRequest req = getCreatePostDataRequest();
        req.setContent("");
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 카테고리없는게시글_작성_실패_BadRequest() throws Exception{
        MediaPostRequest req = getCreatePostDataRequest();
        req.setCategory("");
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 게시글_삭제_성공() throws Exception{
        //given
        Long id = 1L;
        doNothing().when(postService).deletePost(id);
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/posts/{id}", id));
        //then
        response.andDo(print())
                .andExpect(status().isOk());
        verify(postService).deletePost(id);
    }

    @Test
    void 게시글_수정_성공() throws Exception{
        //given
        PostRequest req = getUpdatePostDataRequest();
        PostResponse expect = getUpdatePostDataResponse();
        Long id = 1L;
        given(postService.updatePost(any(PostRequest.class), any(Long.class))).willReturn(expect);
        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/posts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(expect.getTitle())))
                .andExpect(jsonPath("$.content", is(expect.getContent())))
                .andExpect(jsonPath("$.category", is(expect.getCategory())));
        verify(postService).updatePost(refEq(req), anyLong());
    }

    @Test
    void 게시글_읽기_성공() throws Exception{
        //given
        MediaPostResponse expect = getCreatePostDataResponse();
        Long id = 1L;
        given(postService.getPost(any(Long.class))).willReturn(expect);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/posts/{id}", id));

        result.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(expect.getTitle())))
                .andExpect(jsonPath("$.content", is(expect.getContent())))
                .andExpect(jsonPath("$.category", is(expect.getCategory())));
    }

    @Test
    void 게시글_목록_읽기_성공() throws Exception{
        //given
        PostListRequest req = getPostListDataRequest();
        Page<PostListResponse> response = getPostListDataResponse();
        List<PostListResponse> expect = response.toList();
        given(postService.getPostList(any(PostListRequest.class))).willReturn(response);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/posts"));
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
