package spring.helper;

import spring.bbs.category.repository.CategoryRepositoryHandler;
import spring.bbs.member.domain.Member;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

public class PostCreator {

    private final PostRepository postRepository;
    private final CategoryRepositoryHandler categoryRepositoryHandler;

    public PostCreator(PostRepository postRepository, CategoryRepositoryHandler categoryRepositoryHandler) {
        this.postRepository = postRepository;
        this.categoryRepositoryHandler = categoryRepositoryHandler;
    }

    public List<Post> createPostList(Member author, int num) {
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

    public Post createPost(Member author, String title, String content) {
        Post post = Post.builder()
            .title(title)
            .content(content)
            .category(categoryRepositoryHandler.findByName("string"))
            .author(author)
            .build();
        return postRepository.saveAndFlush(post);
    }

    public Post createPost(Member author, String title) {
        Post post = Post.builder()
            .title(title)
            .content("createTestContent")
            .category(categoryRepositoryHandler.findByName("string"))
            .author(author)
            .build();
        return postRepository.saveAndFlush(post);
    }

    public Post createPost(Member author) {
        Post post = Post.builder()
            .title("createPostTitle")
            .content("createPostContent")
            .category(categoryRepositoryHandler.findByName("string"))
            .author(author)
            .build();
        return postRepository.saveAndFlush(post);
    }
}
