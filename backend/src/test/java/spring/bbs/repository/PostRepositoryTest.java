package spring.bbs.repository;

import jakarta.annotation.PostConstruct;
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
import spring.ProfileConfiguration;
import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepository;
import spring.config.TestConfig;
import spring.helper.MemberCreator;
import spring.helper.PostCreator;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@ProfileConfiguration
public class PostRepositoryTest {
    private static final String MEMBER_NAME = "PostTestUser";
    
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepositoryHandler categoryRepositoryHandler;
    
    private MemberCreator memberCreator;
    private PostCreator postCreator;

    private int PAGE_SIZE = 10;

    @AfterEach
    void tearDown() {
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @PostConstruct
    void init(){
        this.memberCreator = new MemberCreator(memberRepository);
        this.postCreator = new PostCreator(postRepository, categoryRepositoryHandler);
    }

    @DisplayName("페이지 번호가 주어지면 해당 페이지에 맞는 글 목록을 조회한다.")
    @Test
    void findAllToPageByPageNum() {
        //given
        Member member = memberCreator.createMember(MEMBER_NAME);
        postCreator.createPost(member, "title1");
        postCreator.createPost(member, "title2");
        postCreator.createPost(member, "title3");
        postCreator.createPostList(member, 10);
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
        Member member = memberCreator.createMember(MEMBER_NAME);
        postCreator.createPostList(member, 10);
        postCreator.createPost(member, "find1");
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
        Member member = memberCreator.createMember(MEMBER_NAME);
        postCreator.createPostList(member, 10);
        postCreator.createPost(member, "RightTitle", "findContent");
        int page = 1;
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").descending());
        //when
        Page<Post> result = postRepository.findAllToPageAndSearchKeywordAndScope("전체", "find", pageable);
        //then
        assertThat(result.get().collect(Collectors.toList())).hasSize(1)
            .extracting("title")
            .containsExactlyInAnyOrder("RightTitle");
    }

    @DisplayName("페이지 번호, 검색 키워드와 검색 범위로 작성자가 주어지면 해당 페이지에 맞고 작성자가 작성한 글 목록을 조회한다.")
    @Test
    void findAllToPageAndSearchKeywordWithAuthorScope() {
        //given
        Member member = memberCreator.createMember(MEMBER_NAME);
        String searchMemberName = "SearchMember";
        Member searchMember = memberCreator.createMember(searchMemberName);
        postCreator.createPostList(member, 3);
        postCreator.createPost(searchMember, "RightTitle");
        int page = 1;
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdTime").descending());
        //when
        Page<Post> result = postRepository.findAllToPageAndSearchKeywordAndScope("작성자", searchMemberName, pageable);
        //then
        assertThat(result.get().collect(Collectors.toList())).hasSize(1)
            .extracting("title")
            .containsExactlyInAnyOrder("RightTitle");
    }
}
