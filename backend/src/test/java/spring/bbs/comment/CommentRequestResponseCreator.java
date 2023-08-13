package spring.bbs.comment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import spring.bbs.written.comment.dto.request.CommentCreateRequest;
import spring.bbs.written.comment.dto.request.CommentListRequest;
import spring.bbs.written.comment.dto.request.CommentUpdateRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommentRequestResponseCreator {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private CommentCreateRequest commentCreateRequest;
    private List<CommentCreateRequest> commentCreateRequestList;
    private CommentUpdateRequest commentUpdateRequest;
    private CommentListRequest commentListRequest;


    public CommentCreateRequest getCreateCommentRequest() throws IOException {
        final String CreateCommentDataPath = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/comment/CreateCommentRequest.json";
        if (commentCreateRequest == null)
            commentCreateRequest = objectMapper
                    .readValue(new File(CreateCommentDataPath), CommentCreateRequest.class);
        return commentCreateRequest;
    }

    public CommentUpdateRequest getUpdateCommentRequest() throws IOException {
        final String UpdateCommentDataPath = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/comment/UpdateCommentRequest.json";
        if (commentUpdateRequest == null)
            commentUpdateRequest = objectMapper
                    .readValue(new File(UpdateCommentDataPath), CommentUpdateRequest.class);
        return commentUpdateRequest;
    }

    public CommentListRequest getCommentListRequest() throws IOException {
        final String CommentListRequestDataPath = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/comment/CommentListRequest.json";
        if (commentListRequest == null)
            commentListRequest = objectMapper
                    .readValue(new File(CommentListRequestDataPath), CommentListRequest.class);
        return commentListRequest;
    }

    public List<CommentCreateRequest> getCreateCommentRequestList() throws IOException {
        final String CreateCommentListDataPath = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/comment/CreateCommentRequestList.json";
        if (commentCreateRequestList == null)
            commentCreateRequestList = objectMapper
                    .readValue(new File(CreateCommentListDataPath), new TypeReference<>() {
                    });
        return commentCreateRequestList;
    }
}
