package spring.bbs.post;

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
import spring.bbs.post.domain.Post;
import spring.bbs.post.dto.request.PostRequest;
import spring.bbs.post.repository.PostRepository;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostIntegrationTests extends AuthenticationTests {

    private final String CreatePostDataPath
            = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/CreatePostData.json";

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
        Member author = memberRepository.findByName(memberName).get();

        List<Post> posts = postRepository.findAllByAuthor(author);
        postRepository.deleteAll(posts);
    }

    @Test
    @DisplayName("게시글 생성")
    public void givenNewPost_thenGetNewPost() throws Exception {
        //given
        PostRequest req = objectMapper
                .readValue(new File(CreatePostDataPath), PostRequest.class);
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

}
