package spring.bbs.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import spring.bbs.written.post.dto.request.MediaPostRequest;
import spring.bbs.written.post.dto.request.PostListRequest;
import spring.bbs.written.post.dto.request.PostRequest;
import spring.bbs.written.post.dto.response.MediaPostResponse;
import spring.bbs.written.post.dto.response.PostListResponse;
import spring.bbs.written.post.dto.response.PostResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class PostRequestCreator {

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected MediaPostRequest createPostRequestData;
    protected MediaPostResponse createPostResponseData;
    protected PostRequest updatePostRequestData;
    protected PostResponse updatePostResponseData;
    protected PostListRequest postListRequestData;
    protected Page<PostListResponse> postListResponseData;


    protected MediaPostRequest getCreatePostDataRequest() throws IOException {
        final String CreatePostDataPath
                = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/CreatePostData.json";
        if (createPostRequestData == null)
            createPostRequestData = objectMapper
                    .readValue(new File(CreatePostDataPath), MediaPostRequest.class);
        return createPostRequestData;
    }

    protected MediaPostResponse getCreatePostDataResponse() throws IOException {
        if (createPostResponseData == null) {
            if (createPostRequestData == null)
                getCreatePostDataRequest();
            createPostResponseData = MediaPostResponse.builder()
                    .title(createPostRequestData.getTitle())
                    .content(createPostRequestData.getContent())
                    .category(createPostRequestData.getCategory())
                    .build();
        }
        return createPostResponseData;
    }

    protected PostRequest getUpdatePostDataRequest() throws IOException {
        final String UpdatePostDataPath
                = "/Users/mjmj/Desktop/bbs/backend/src/test/resources/post/UpdatePostData.json";
        if (updatePostRequestData == null)
            updatePostRequestData = objectMapper
                    .readValue(new File(UpdatePostDataPath), PostRequest.class);
        return updatePostRequestData;
    }

    protected PostResponse getUpdatePostDataResponse() throws IOException {
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

    protected PostListRequest getPostListDataRequest() throws IOException {
        if(postListRequestData == null){
            postListRequestData =
                    new PostListRequest(1, "string", null, null);
        }
        return postListRequestData;
    }

    protected Page<PostListResponse> getPostListDataResponse() throws IOException {
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
