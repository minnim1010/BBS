package spring.bbs.post.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepository;
import spring.bbs.util.RoleType;
import spring.config.TestConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
public class PostRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepositoryHandler categoryRepositoryHandler;

    private int PAGE_SIZE = 10;

    @AfterEach
    void tearDown() {
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("페이지 번호가 주어지면 해당 페이지에 맞는 글 목록을 조회한다.")
    @Test
    void findAllToPageByPageNum() {
        //given
        Member member = createMember("PostTestMember");
        createPost(member, "title1");
        createPost(member, "title2");
        createPost(member, "title3");
        createPostList(member, 10);
        int page = 2;
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").descending());
        //when
        Page<Post> result = postRepository.findAllToPage(pageable);
        //then
        assertThat(result.get().collect(Collectors.toList())).hasSize(3)
            .extracting("title")
            .containsExactly("title3", "title2", "title1");
    }

    @DisplayName("페이지 번호, 검색 키워드와 검색 범위로 제목이 주어지면 해당 페이지에 맞고 제목에서 키워드를 포함하는 글 목록을 조회한다.")
    @Test
    void findAllToPageAndSearchKeywordWithTitleScope() {
        //given
        Member member = createMember("PostTestMember");
        createPostList(member, 10);
        createPost(member, "find1");
        int page = 1;
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").descending());
        //when
        Page<Post> result = postRepository.findAllToPageAndSearchKeywordAndScope("제목", "find", pageable);
        //then
        assertThat(result.get().collect(Collectors.toList())).hasSize(1)
            .extracting("title")
            .containsExactlyInAnyOrder("find1");
    }

    @DisplayName("페이지 번호, 검색 키워드와 검색 범위로 제목+내용이 주어지면 해당 페이지에 맞고 제목+내용에서 키워드를 포함하는 글 목록을 조회한다.")
    @Test
    void findAllToPageAndSearchKeywordWithTitleContentScope() {
        //given
        Member member = createMember("PostTestMember");
        createPostList(member, 10);
        createPost(member, "RightTitle", "findContent");
        int page = 1;
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").descending());
        //when
        Page<Post> result = postRepository.findAllToPageAndSearchKeywordAndScope("제목+내용", "find", pageable);
        //then
        assertThat(result.get().collect(Collectors.toList())).hasSize(1)
            .extracting("title")
            .containsExactlyInAnyOrder("RightTitle");
    }

    @DisplayName("페이지 번호, 검색 키워드와 검색 범위로 작성자가 주어지면 해당 페이지에 맞고 작성자가 작성한 글 목록을 조회한다.")
    @Test
    void findAllToPageAndSearchKeywordWithAuthorScope() {
        //given
        Member member = createMember("PostTestMember");
        Member searchMember = createMember("SearchMember");
        createPostList(member, 3);
        createPost(searchMember, "RightTitle");
        int page = 1;
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").descending());
        //when
        Page<Post> result = postRepository.findAllToPageAndSearchKeywordAndScope("작성자", "SearchMember", pageable);
        //then
        assertThat(result.get().collect(Collectors.toList())).hasSize(1)
            .extracting("title")
            .containsExactlyInAnyOrder("RightTitle");
    }

    private List<Post> createPostList(Member author, int num) {
        List<Post> postList = new ArrayList<>(num);
        for (int i = 1; i <= num; i++) {
            postList.add(Post.builder()
                .title("createTestTitle" + i)
                .content("createTestContent" + i)
                .category(categoryRepositoryHandler.findByName("string"))
                .author(author)
                .build()
            );
        }
        return postRepository.saveAllAndFlush(postList);
    }

    private Post createPost(Member author, String title, String content) {
        Post post = Post.builder()
            .title(title)
            .content(content)
            .category(categoryRepositoryHandler.findByName("string"))
            .author(author)
            .build();
        return postRepository.saveAndFlush(post);
    }

    private Post createPost(Member author, String title) {
        Post post = Post.builder()
            .title(title)
            .content("createTestContent")
            .category(categoryRepositoryHandler.findByName("string"))
            .author(author)
            .build();
        return postRepository.saveAndFlush(post);
    }

    private Member createMember(String name) {
        Member newMember = Member.builder()
            .name(name)
            .password("password")
            .email(name + "@test.com")
            .isEnabled(true)
            .authority(Enum.valueOf(Authority.class, RoleType.user))
            .build();
        return memberRepository.save(newMember);
    }
}
