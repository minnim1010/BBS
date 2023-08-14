package spring.bbs.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import spring.bbs.category.domain.Category;
import spring.bbs.category.repository.CategoryRepository;
import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.repository.CommentRepository;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.member.domain.Member;
import spring.bbs.member.repository.MemberRepository;
import spring.bbs.post.domain.Post;
import spring.bbs.post.repository.PostRepository;

@Component
@RequiredArgsConstructor
public class CommonUtil {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;

    public Post getPost(long postId){
        return postRepository.findById(postId).orElseThrow(
                () -> new DataNotFoundException("Post doesn't exist."));
    }

    public Category getCategory(String name){
        return categoryRepository.findByName(name).orElseThrow(
                () -> new DataNotFoundException("Category doesn't exist."));
    }

    public Member getMember(String authorName){
        return memberRepository.findByName(authorName).orElseThrow(
                () -> new DataNotFoundException("Member doesn't exist."));
    }

    public String getCurrentLoginedUser(){
        return AuthenticationUtil.getCurrentMemberName().orElseThrow(
                () -> new BadCredentialsException("Can't get current logined user."));
    }

    public Comment getComment(long commentId){
        return commentRepository.findById(commentId).orElseThrow(
                () -> new DataNotFoundException("Comment doesn't exist."));
    }

    public void validAuthor(String authorName){
        if(!authorName.equals(this.getCurrentLoginedUser()))
            throw new RuntimeException("No valid author.");
    }
}
