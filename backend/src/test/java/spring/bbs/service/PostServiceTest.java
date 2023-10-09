package spring.bbs.service;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import spring.IntegrationTestConfig;
import spring.bbs.category.domain.Category;
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.common.exceptionhandling.exception.DataNotFoundException;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.controller.dto.request.PostListRequest;
import spring.bbs.post.controller.dto.response.PostListResponse;
import spring.bbs.post.controller.dto.response.PostResponse;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepository;
import spring.bbs.post.service.PostService;
import spring.bbs.post.service.dto.PostDeleteServiceRequest;
import spring.bbs.post.service.dto.PostServiceRequest;
import spring.bbs.post.service.dto.PostUpdateServiceRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PostServiceTest extends IntegrationTestConfig {

    private static final String MEMBER_NAME = "PostTestUser";
    private static final String CATEGORY_NAME = "string";

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepositoryHandler categoryRepositoryHandler;

    @AfterEach
    void tearDown() {
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("특정 게시글 조회를 요청할 때 ")
    class getPost {
        @DisplayName("해당 게시글이 존재하면 조회한다.")
        @Test
        void successReturnPost() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title1");

            //when
            PostResponse response = postService.getPost(post.getId());

            //then
            assertThat(response)
                .extracting("id", "title", "author.name")
                .contains(post.getId(), "title1", MEMBER_NAME);
        }

        @DisplayName("해당 게시글이 존재하지 않으면 조회할 수 없다.")
        @Test
        void failWithNonExistedPost() {
            //given
            //when then
            assertThatThrownBy(() -> postService.getPost(1L))
                .isInstanceOf(DataNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("게시글 목록을 요청할 떄 ")
    class GetPostList {
        @DisplayName("검색 키워드가 없으면 최근 작성된 순서로 게시글 목록을 조회한다.")
        @Test
        void successReturnRecentlyWrittenPosts() {
            //given
            Member member = createMember(MEMBER_NAME);

            createPost(member, "title1");
            createPost(member, "title2");
            createPost(member, "title3");

            PostListRequest request = PostListRequest.builder()
                .page(1)
                .category(CATEGORY_NAME)
                .build();

            //when
            Page<PostListResponse> response = postService.getPostList(request);

            //then
            assertThat(response).hasSize(3)
                .extracting("title", "author.name")
                .containsExactly(
                    Tuple.tuple("title3", MEMBER_NAME),
                    Tuple.tuple("title2", MEMBER_NAME),
                    Tuple.tuple("title1", MEMBER_NAME)
                );
        }

        @Nested
        @DisplayName("검색 키워드가 있으면 ")
        class SearchKeywordExists {
            @DisplayName("검색 범위에 검색 키워드가 포함된, 최근 작성된 게시글 목록을 조회한다.")
            @Test
            void successReturnSearchedPostList() {
                //given
                Member member = createMember(MEMBER_NAME);

                createPost(member, "title2");
                createPost(member, "search1");
                createPost(member, "search3");

                PostListRequest request = PostListRequest.builder()
                    .page(1)
                    .searchScope("제목")
                    .searchKeyword("search")
                    .category(CATEGORY_NAME)
                    .build();

                //when
                Page<PostListResponse> response = postService.getPostList(request);

                //then
                assertThat(response).hasSize(2)
                    .extracting("title", "author.name")
                    .containsExactly(
                        Tuple.tuple("search3", MEMBER_NAME),
                        Tuple.tuple("search1", MEMBER_NAME)
                    );
            }

            @DisplayName("지원하지 않는 검색 범위라면 게시글 목록을 조회할 수 없다.")
            @Test
            void failWithNotSupportedScope() {
                //given
                Member member = createMember(MEMBER_NAME);

                createPost(member, "search1");
                createPost(member, "title2");
                createPost(member, "search3");

                PostListRequest request = PostListRequest.builder()
                    .page(1)
                    .searchScope("invalidScope")
                    .searchKeyword("search")
                    .category(CATEGORY_NAME)
                    .build();

                //when then
                assertThatThrownBy(() -> postService.getPostList(request))
                    .isInstanceOf(IllegalStateException.class);
            }
        }
    }

    @Nested
    @DisplayName("게시글 작성을 요청할 때 ")
    class CreatePost {
        @DisplayName("새로운 게시글을 생성한다.")
        @Test
        void returnNewPost() {
            //given
            createMember(MEMBER_NAME);

            String title = "title";
            String content = "content";

            PostServiceRequest request = PostServiceRequest.builder()
                .title(title)
                .content(content)
                .category(CATEGORY_NAME)
                .curMemberName(MEMBER_NAME)
                .build();

            //when
            PostResponse response = postService.createPost(request);

            //then
            assertThat(response)
                .extracting("title", "content", "author.name", "category")
                .contains(title, content, MEMBER_NAME, CATEGORY_NAME);

            List<Post> result = postRepository.findAll();
            assertThat(result).hasSize(1);
        }

        @DisplayName("유효하지 않은 사용자라면, 게시글을 생성할 수 없다.")
        @Test
        void failWithInvalidUser() {
            //given
            String title = "title";
            String content = "content";

            PostServiceRequest request = PostServiceRequest.builder()
                .title(title)
                .content(content)
                .category(CATEGORY_NAME)
                .curMemberName("invalidMember")
                .build();

            //when then
            assertThatThrownBy(() -> postService.createPost(request))
                .isInstanceOf(DataNotFoundException.class);

            List<Post> result = postRepository.findAll();
            assertThat(result).isEmpty();
        }

        @DisplayName("유효하지 않은 카테고리라면, 게시글을 생성할 수 없다.")
        @Test
        void failWithInvalidCategory() {
            //given
            createMember(MEMBER_NAME);

            String title = "title";
            String content = "content";

            PostServiceRequest request = PostServiceRequest.builder()
                .title(title)
                .content(content)
                .category("invalidCategory")
                .curMemberName(MEMBER_NAME)
                .build();

            //when then
            assertThatThrownBy(() -> postService.createPost(request))
                .isInstanceOf(DataNotFoundException.class);

            List<Post> result = postRepository.findAll();
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("게시글 수정을 요청할 때 ")
    class UpdatePost {
        @DisplayName("게시글을 수정한다.")
        @Test
        void successReturnModifiedPost() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title1");

            PostUpdateServiceRequest request = PostUpdateServiceRequest.builder()
                .id(post.getId())
                .title("updateTitle")
                .content("updateContent")
                .category("Java")
                .curMemberName(MEMBER_NAME)
                .build();

            //when
            PostResponse response = postService.updatePost(request);

            //then
            assertThat(response)
                .extracting("title", "content", "category")
                .contains("updateTitle", "updateContent", "Java");
        }

        @DisplayName("작성자와 현재 로그인한 유저가 다르면 게시글을 수정할 수 없다.")
        @Test
        void failWithInvalidAuthor() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title1");

            PostUpdateServiceRequest request = PostUpdateServiceRequest.builder()
                .id(post.getId())
                .title("updateTitle")
                .content("updateContent")
                .category("Java")
                .curMemberName("invalidMember")
                .build();

            //when then
            assertThatThrownBy(() -> postService.updatePost(request))
                .isInstanceOf(AccessDeniedException.class);
        }

        @DisplayName("해당 게시글이 존재하지 않으면 게시글을 수정할 수 없다.")
        @Test
        void failWithNonExistedPost() {
            //given
            createMember(MEMBER_NAME);

            PostUpdateServiceRequest request = PostUpdateServiceRequest.builder()
                .id(1L)
                .title("updateTitle")
                .content("updateContent")
                .category("Java")
                .curMemberName("invalidMember")
                .build();

            //when then
            assertThatThrownBy(() -> postService.updatePost(request))
                .isInstanceOf(DataNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("게시글 삭제를 요청할 때 ")
    class DeletePost {
        @DisplayName("게시글을 삭제한다.")
        @Test
        void success() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title1");

            PostDeleteServiceRequest request = PostDeleteServiceRequest.builder()
                .id(post.getId())
                .curMemberName(MEMBER_NAME)
                .build();

            //when
            postService.deletePost(request);

            //then
            List<Post> posts = postRepository.findAll();
            assertThat(posts).isEmpty();
        }

        @DisplayName("작성자와 현재 로그인한 유저가 다르면 게시글을 삭제할 수 없다.")
        @Test
        void failWithInvalidAuthor() {
            //given
            Member member = createMember(MEMBER_NAME);

            Post post = createPost(member, "title1");

            PostDeleteServiceRequest request = PostDeleteServiceRequest.builder()
                .id(post.getId())
                .curMemberName("invalidMember")
                .build();

            //when then
            assertThatThrownBy(() -> postService.deletePost(request))
                .isInstanceOf(AccessDeniedException.class);
        }

        @DisplayName("해당 게시글이 존재하지 않으면 게시글을 삭제할 수 없다.")
        @Test
        void failWithNonExistedPost() {
            //given
            createMember(MEMBER_NAME);

            PostDeleteServiceRequest request = PostDeleteServiceRequest.builder()
                .id(1L)
                .curMemberName(MEMBER_NAME)
                .build();

            //when then
            assertThatThrownBy(() -> postService.deletePost(request))
                .isInstanceOf(DataNotFoundException.class);
        }
    }

    private Member createMember(String name) {
        Member newMember = Member.builder()
            .name(name)
            .password(name)
            .email(name + "@test.com")
            .authority(Authority.ROLE_USER)
            .build();

        return memberRepository.save(newMember);
    }

    private Post createPost(Member member, String title) {
        Category category = categoryRepositoryHandler.findByName("string");

        Post post = Post.builder()
            .author(member)
            .title(title)
            .content("content")
            .category(category)
            .build();

        return postRepository.save(post);
    }
}
