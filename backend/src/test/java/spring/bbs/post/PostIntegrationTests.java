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
import spring.bbs.post.domain.Category;
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.repository.PostRepository;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.bbs.post.dto.util.RequestToPost.convertCreateRequestToPost;

public class PostIntegrationTests extends AuthenticationTests {

    private final String CreatePostDataPath
            = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/CreatePostData.json";
    private final String PostListDataPath
            = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/PostListData.json";
    private final String UpdatePostDataPath
            = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/UpdatePostData.json";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostRepository postRepository;

    private final String username = "postTest";

    public PostIntegrationTests(){
        setMemberName(username);
    }

    @AfterEach
    private void deletePost(){
        memberRepository.findByName(memberName).ifPresent(author ->
        {
            List<Post> posts = postRepository.findAllByAuthor(author);
            postRepository.deleteAll(posts);
        });
    }

    private Post createPost() throws Exception{
        PostRequest req = objectMapper
                .readValue(new File(CreatePostDataPath), PostRequest.class);

        Member author = getExistedMember();
        return postRepository.save(convertCreateRequestToPost(req, author, new Category(req.getCategory())));
    }

    private Member getExistedMember() {
        return memberRepository.findByName(memberName)
                .orElseGet(() -> createMember(memberName));
    }

    private List<Post> createPostList() throws Exception{
        List<PostRequest> postRequestList = objectMapper
                .readValue(new File(PostListDataPath), new TypeReference<>() {
                });

        Member author = getExistedMember();
        List<Post> postList = postRequestList.stream()
                .map(p -> convertCreateRequestToPost(p, author, new Category(p.getCategory())))
                .collect(Collectors.toList());

        return postRepository.saveAll(postList);
    }

    @Test
    @DisplayName("게시글 조회 성공")
    void givenExistedPost_thenGetPost() throws Exception {
        //given
        Post post = createPost();
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/posts/{id}", post.getId()));

        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(post.getTitle())))
                .andExpect(jsonPath("$.content", is(post.getContent())))
                .andExpect(jsonPath("$.category", is(post.getCategory().getName())));
    }

    @Test
    @DisplayName("게시글 목 조회 성공")
    void givenExistedPosts_thenGetPostList() throws Exception {
        //given
        List<Post> post = createPostList();
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/posts"));

        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is(post.get(2).getTitle())))
                .andExpect(jsonPath("$[0].authorResponse.name", is(post.get(2).getAuthor().getName())))
                .andExpect(jsonPath("$[1].title", is(post.get(1).getTitle())))
                .andExpect(jsonPath("$[1].authorResponse.name", is(post.get(1).getAuthor().getName())))
                .andExpect(jsonPath("$[2].title", is(post.get(0).getTitle())))
                .andExpect(jsonPath("$[2].authorResponse.name", is(post.get(0).getAuthor().getName())));
    }


    @Test
    @DisplayName("게시글 생성 성공")
    void givenNewPost_thenGetNewPost() throws Exception {
        //given
        PostRequest req = objectMapper
                .readValue(new File(UpdatePostDataPath), PostRequest.class);
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
        Long postId = createPost().getId();

        PostRequest req = objectMapper
                .readValue(new File(CreatePostDataPath), PostRequest.class);
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
    @DisplayName("게시글 삭제 성공")
    void givenExistedPost_thenPostDelete() throws Exception {
        //given
        Long postId = createPost().getId();
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

}
