package spring.bbs.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import spring.bbs.AuthenticationTests;
import spring.bbs.member.domain.Member;
import spring.bbs.util.CommonUtil;
import spring.bbs.written.post.domain.Post;
import spring.bbs.written.post.dto.request.PostRequest;
import spring.bbs.written.post.dto.util.RequestToPostConvertor;
import spring.bbs.written.post.repository.PostRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostIntegrationTests extends AuthenticationTests {

    private final String username = "postTestUser1";
    private final String otherUsername = "postTestUser2";

    private PostRequest createPostData;
    private PostRequest updatePostData;
    private List<PostRequest> postListData;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommonUtil util;

    public PostIntegrationTests(){
        setMemberName(username);
    }

    @AfterEach
    void deletePost(){
        memberRepository.findByName(memberName).ifPresent(author ->
        {
            List<Post> posts = postRepository.findAllByAuthor(author);
            postRepository.deleteAll(posts);
        });
    }

    private Post createPostByAuthor(String memberName) throws Exception{
        PostRequest req = getCreatePostDataRequest();
        Member author = getMember(memberName);
        Post post = RequestToPostConvertor.of(req, author, util.getCategory(req.getCategory()));
        return postRepository.save(post);
    }

    private List<Post> createPostList() throws Exception{
        List<PostRequest> postRequestList = getPostListDataRequest();
        Member author = getMember(username);
        List<Post> postList = postRequestList.stream()
                .map(p -> RequestToPostConvertor.of(p, author, util.getCategory(p.getCategory())))
                .collect(Collectors.toList());

        return postRepository.saveAll(postList);
    }

    private Member getMember(String name) {
        return memberRepository.findByName(name)
                .orElseGet(() -> createMember(name));
    }

    private PostRequest getCreatePostDataRequest() throws IOException {
        final String CreatePostDataPath
                = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/CreatePostData.json";
        if(createPostData == null)
            createPostData = objectMapper
                    .readValue(new File(CreatePostDataPath), PostRequest.class);
        return createPostData;
    }

    private PostRequest getUpdatePostDataRequest() throws IOException {
        final String UpdatePostDataPath
                = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/UpdatePostData.json";
        if(updatePostData == null)
            updatePostData = objectMapper
                    .readValue(new File(UpdatePostDataPath), PostRequest.class);
        return updatePostData;
    }

    private List<PostRequest> getPostListDataRequest() throws IOException {
        final String PostListDataPath
                = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/PostListData.json";
        if(postListData == null)
            postListData = objectMapper
                    .readValue(new File(PostListDataPath), new TypeReference<>() {});
        return postListData;
    }

    @Test
    @DisplayName("게시글 조회 성공")
    void givenExistedPost_thenGetPost() throws Exception {
        //given
        Post post = createPostByAuthor(username);
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/posts/{id}", post.getId()));

        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(post.getTitle())))
                .andExpect(jsonPath("$.content", is(post.getContent())))
                .andExpect(jsonPath("$.category", is(post.getCategory().getName())));
    }

    @Test
    @DisplayName("게시글 조회 실패: 존재하지 않는 게시글")
    void givenNonExistedPost_thenDataNotFoundError() throws Exception {
        //given
        Post post = createPostByAuthor(username);
        postRepository.delete(post);
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/posts/{id}", post.getId()));

        response.andDo(print()).
                andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void givenExistedPosts_thenGetPostList() throws Exception {
        //given
        List<Post> post = createPostList();
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/posts"));

        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].title", is(post.get(2).getTitle())))
                .andExpect(jsonPath("$.content.[0].authorResponse.name", is(post.get(2).getAuthor().getName())))
                .andExpect(jsonPath("$.content.[1].title", is(post.get(1).getTitle())))
                .andExpect(jsonPath("$.content.[1].authorResponse.name", is(post.get(1).getAuthor().getName())))
                .andExpect(jsonPath("$.content.[2].title", is(post.get(0).getTitle())))
                .andExpect(jsonPath("$.content.[2].authorResponse.name", is(post.get(0).getAuthor().getName())));
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void givenNewPost_thenGetNewPost() throws Exception {
        //given
        PostRequest req = getCreatePostDataRequest();
        String token = getJwtToken();
        String tokenHeader = getJwtTokenHeader(token);

        ResultActions response = mockMvc.perform(post("/api/v1/posts")
                .header(AUTHENTICATION_HEADER, tokenHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(req.getTitle())))
                .andExpect(jsonPath("$.content", is(req.getContent())))
                .andExpect(jsonPath("$.category", is(req.getCategory())));
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void givenExistedPost_thenGetUpdatedPost() throws Exception {
        //given
        Long postId = createPostByAuthor(username).getId();

        PostRequest req = getUpdatePostDataRequest();
        String token = getJwtToken();
        String tokenHeader = getJwtTokenHeader(token);
        //when
        ResultActions response = mockMvc.perform(patch("/api/v1/posts/{id}", postId)
                .header(AUTHENTICATION_HEADER, tokenHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(req.getTitle())))
                .andExpect(jsonPath("$.content", is(req.getContent())))
                .andExpect(jsonPath("$.category", is(req.getCategory())));
    }

    @Test
    @DisplayName("게시글 수정 실패: 존재하지 않는 게시글")
    void givenNonExistedPost_whenUpdate_thenDataNotFoundError() throws Exception {
        //given
        Post post = createPostByAuthor(username);
        postRepository.delete(post);

        String token = getJwtToken();
        String tokenHeader = getJwtTokenHeader(token);

        PostRequest req = getUpdatePostDataRequest();
        //when
        ResultActions response = mockMvc.perform(patch("/api/v1/posts/{id}", post.getId())
                        .header(AUTHENTICATION_HEADER, tokenHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print()).
                andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게시글 수정 실패: 유효하지 않은 사용자")
    void givenExistedPost_AndOtherUser_whenUpdate_thenForbiddenError() throws Exception {
        //given
        Long postId = createPostByAuthor(username).getId();

        String token = getJwtToken(otherUsername);
        String tokenHeader = getJwtTokenHeader(token);

        PostRequest req = getUpdatePostDataRequest();
        //when
        ResultActions response = mockMvc.perform(patch("/api/v1/posts/{id}", postId)
                .header(AUTHENTICATION_HEADER, tokenHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        //then
        response.andDo(print()).
                andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("게시글 삭제 성공")
    void givenExistedPost_thenPostDelete() throws Exception {
        //given
        Long postId = createPostByAuthor(username).getId();
        String token = getJwtToken();
        String tokenHeader = getJwtTokenHeader(token);
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                .header(AUTHENTICATION_HEADER, tokenHeader));
        //then
        response.andDo(print()).
                andExpect(status().isOk());
        assert(postRepository.findById(postId).isEmpty());
    }

    @Test
    @DisplayName("게시글 삭제 실패: 존재하지 않는 게시글")
    void givenNonExistedPost_whenDelete_thenDataNotFoundError() throws Exception {
        //given
        Post post = createPostByAuthor(username);
        postRepository.delete(post);

        String token = getJwtToken();
        String tokenHeader = getJwtTokenHeader(token);
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/posts/{id}", post.getId())
                .header(AUTHENTICATION_HEADER, tokenHeader)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        response.andDo(print()).
                andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("게시글 삭제 실패: 유효하지 않은 사용자")
    void givenExistedPost_AndOtherUser_whenDelete_thenForbiddenError() throws Exception {
        //given
        Long postId = createPostByAuthor(username).getId();

        String token = getJwtToken(otherUsername);
        String tokenHeader = getJwtTokenHeader(token);
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                .header(AUTHENTICATION_HEADER, tokenHeader)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        response.andDo(print()).
                andExpect(status().isForbidden());
    }
}
