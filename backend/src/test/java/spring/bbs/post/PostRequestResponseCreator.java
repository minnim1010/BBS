package spring.bbs.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import spring.bbs.written.post.dto.request.PostListRequest;
import spring.bbs.written.post.dto.request.PostRequest;
import spring.bbs.written.post.dto.response.PostListResponse;
import spring.bbs.written.post.dto.response.PostResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PostRequestResponseCreator {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private PostRequest createPostRequestData;
    private PostResponse createPostResponseData;
    private PostRequest updatePostRequestData;
    private PostResponse updatePostResponseData;
    private PostListRequest postListRequestData;
    private Page<PostListResponse> postListResponseData;

    public PostRequest getCreatePostDataRequest() throws IOException {
        final String CreatePostDataPath
                = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/CreatePostData.json";
        if (createPostRequestData == null)
            createPostRequestData = objectMapper
                    .readValue(new File(CreatePostDataPath), PostRequest.class);
        return createPostRequestData;
    }

    public PostResponse getCreatePostDataResponse() throws IOException {
        if (createPostResponseData == null) {
            if (createPostRequestData == null)
                getCreatePostDataRequest();
            createPostResponseData = PostResponse.builder()
                    .title(createPostRequestData.getTitle())
                    .content(createPostRequestData.getContent())
                    .category(createPostRequestData.getCategory())
                    .build();
        }
        return createPostResponseData;
    }

    public PostRequest getUpdatePostDataRequest() throws IOException {
        final String UpdatePostDataPath
                = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/UpdatePostData.json";
        if (updatePostRequestData == null)
            updatePostRequestData = objectMapper
                    .readValue(new File(UpdatePostDataPath), PostRequest.class);
        return updatePostRequestData;
    }

    public PostResponse getUpdatePostDataResponse() throws IOException {
        if (updatePostResponseData == null) {
            if (updatePostRequestData == null)
                getUpdatePostDataRequest();
            updatePostResponseData = PostResponse.builder()
                    .title(updatePostRequestData.getTitle())
                    .content(updatePostRequestData.getContent())
                    .category(updatePostRequestData.getCategory())
                    .build();
        }
        return updatePostResponseData;
    }

    public PostListRequest getPostListDataRequest(){
        if(postListRequestData == null){
            postListRequestData =
                    new PostListRequest(1, "string", null, null);
        }
        return postListRequestData;
    }

    public Page<PostListResponse> getPostListDataResponse() throws IOException {
        final String PostListDataPath
                = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/PostListResponseData.json";
        if (postListResponseData == null){
            List<PostListResponse> postList = objectMapper
                    .readValue(new File(PostListDataPath), new TypeReference<>() {
                    });
            PageRequest pageRequest = PageRequest.of(0, 10);
            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), postList.size());
            postListResponseData = new PageImpl<>(postList.subList(start, end), pageRequest, postList.size());
        }

        return postListResponseData;
    }
}
