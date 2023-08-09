package spring.bbs.comment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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
import spring.bbs.written.comment.domain.Comment;
import spring.bbs.written.comment.dto.request.CommentCreateRequest;
import spring.bbs.written.comment.dto.request.CommentListRequest;
import spring.bbs.written.comment.dto.request.CommentUpdateRequest;
import spring.bbs.written.comment.repository.CommentRepository;
import spring.bbs.written.post.domain.Post;
import spring.bbs.written.post.dto.request.MediaPostRequest;
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
import static spring.bbs.written.comment.dto.util.RequestToComment.convertRequestToComment;

public class CommentIntegrationTests extends AuthenticationTests {

    private final String username = "CommentTestUser1";
    private final String otherUserName = "CommentTestUser2";

    private CommentCreateRequest commentCreateRequest;
    private List<CommentCreateRequest> commentCreateRequestList;
    private CommentUpdateRequest commentUpdateRequest;
    private CommentListRequest commentListRequest;

    private Post testPost;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommonUtil util;

    public CommentIntegrationTests() {
        setMemberName(username);
    }

    @PostConstruct
    protected void init() {
        super.init();
        createPost();
    }

    void createPost() {
        final String CreatePostDataPath = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/CreatePostData.json";
        try {
            MediaPostRequest req = objectMapper
                    .readValue(new File(CreatePostDataPath), MediaPostRequest.class);
            Member author = getMember(memberName);
            Post post = RequestToPostConvertor.of(req, author, util.getCategory(req.getCategory()));
            this.testPost = postRepository.save(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void deleteComment() {
        memberRepository.findByName(memberName).ifPresent(author -> {
            List<Comment> comments = commentRepository.findAllByAuthor(author);
            commentRepository.deleteAll(comments);
        });
    }

    private Comment createCommentByAuthor(String authorName) throws IOException {
        CommentCreateRequest req = getCreateCommentRequest();
        Member author = getMember(authorName);
        Comment comment = convertRequestToComment(req.getContent(), author, testPost, null);
        return commentRepository.save(comment);
    }

    private List<Comment> createCommentCreateRequestList() throws IOException {
        Member author = getMember(memberName);
        List<Comment> commentList = getCreateCommentRequestList().stream()
                .map((req) -> convertRequestToComment(req.getContent(), author, testPost, null))
                .collect(Collectors.toList());
        return commentRepository.saveAll(commentList);
    }

    private Member getMember(String name) {
        return memberRepository.findByName(name)
                .orElseGet(() -> createMember(name));
    }

    private CommentCreateRequest getCreateCommentRequest() throws IOException {
        final String CreateCommentDataPath = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/comment/CreateCommentRequest.json";
        if (commentCreateRequest == null)
            commentCreateRequest = objectMapper
                    .readValue(new File(CreateCommentDataPath), CommentCreateRequest.class);
        return commentCreateRequest;
    }

    private CommentUpdateRequest getUpdateCommentRequest() throws IOException {
        final String UpdateCommentDataPath = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/comment/UpdateCommentRequest.json";
        if (commentUpdateRequest == null)
            commentUpdateRequest = objectMapper
                    .readValue(new File(UpdateCommentDataPath), CommentUpdateRequest.class);
        return commentUpdateRequest;
    }

    private CommentListRequest getCommentListRequest() throws IOException {
        final String CommentListRequestDataPath = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/comment/CommentListRequest.json";
        if (commentListRequest == null)
            commentListRequest = objectMapper
                    .readValue(new File(CommentListRequestDataPath), CommentListRequest.class);
        return commentListRequest;
    }

    private List<CommentCreateRequest> getCreateCommentRequestList() throws IOException {
        final String CreateCommentListDataPath = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/comment/CreateCommentRequestList.json";
        if (commentCreateRequestList == null)
            commentCreateRequestList = objectMapper
                    .readValue(new File(CreateCommentListDataPath), new TypeReference<>() {
                    });
        return commentCreateRequestList;
    }

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void givenCommentList_thenGetCommentList() throws Exception {
        // given
        List<Comment> commentList = createCommentCreateRequestList();
        CommentListRequest req = getCommentListRequest();
        req.setPostId(testPost.getId());
        // when
        ResultActions response = mockMvc.perform(get("/api/v1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", String.valueOf(req.getPage()))
                .param("postId", String.valueOf(req.getPostId()))
                .param("searchKeyword", String.valueOf(req.getKeyword())));
        // then
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content", is(commentList.get(2).getContent())))
                .andExpect(jsonPath("$[1].content", is(commentList.get(1).getContent())))
                .andExpect(jsonPath("$[2].content", is(commentList.get(0).getContent())));
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void givenNewComment_thenGetNewComment() throws Exception {
        // given
        CommentCreateRequest req = getCreateCommentRequest();
        req.setPostId(testPost.getId());
        String tokenHeader = getJwtTokenHeader(getJwtToken());
        // when
        ResultActions response = mockMvc.perform(post("/api/v1/comments")
                .header(AUTHENTICATION_HEADER, tokenHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        // then
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is(req.getContent())));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void givenExistedComment_thenGetUpdatedComment() throws Exception {
        // given
        Comment comment = createCommentByAuthor(memberName);
        CommentUpdateRequest req = getUpdateCommentRequest();
        String tokenHeader = getJwtTokenHeader(getJwtToken());
        // when
        ResultActions response = mockMvc.perform(patch("/api/v1/comments/{id}", comment.getId())
                .header(AUTHENTICATION_HEADER, tokenHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        // then
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is(req.getContent())));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void givenExistedComment_thenCommentDelete() throws Exception {
        // given
        Comment comment = createCommentByAuthor(memberName);
        String tokenHeader = getJwtTokenHeader(getJwtToken());
        // when
        ResultActions response = mockMvc.perform(delete("/api/v1/comments/{id}", comment.getId())
                .header(AUTHENTICATION_HEADER, tokenHeader)
                .contentType(MediaType.APPLICATION_JSON));
        // then
        response.andDo(print()).andExpect(status().isOk());
        assert (commentRepository.findById(comment.getId()).isEmpty());
    }

}
