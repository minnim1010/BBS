package spring.helper;

import spring.bbs.comment.domain.Comment;
import spring.bbs.comment.repository.CommentRepository;
import spring.bbs.member.domain.Member;
import spring.bbs.post.domain.Post;

import java.util.ArrayList;
import java.util.List;

public class CommentCreator {

    private final CommentRepository commentRepository;

    public CommentCreator(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> createCommentList(Post post, Member author, int num) {
        List<Comment> commentList = new ArrayList<>(num);
        for (int i = 1; i <= num; i++) {
            commentList.add(Comment.builder()
                .content("testContent" + i)
                .author(author)
                .post(post)
                .parentComment(null)
                .build());
        }
        return commentRepository.saveAllAndFlush(commentList);
    }

    public Comment createComment(Post post, Member author, String content, Comment parentComment, int order) {
        Comment comment = Comment.of(content, author, post, parentComment, order);
        return commentRepository.save(comment);
    }

    public Comment createComment(Post post, Member author, String content) {
        Comment comment = Comment.of(content, author, post);
        return commentRepository.save(comment);
    }
}
