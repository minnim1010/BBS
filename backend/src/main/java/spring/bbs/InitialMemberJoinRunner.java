package spring.bbs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import spring.bbs.comment.controller.dto.request.CommentCreateRequest;
import spring.bbs.comment.service.CommentService;
import spring.bbs.member.controller.dto.JoinRequest;
import spring.bbs.member.controller.dto.JoinResponse;
import spring.bbs.member.service.MemberService;
import spring.bbs.post.controller.dto.request.PostRequest;
import spring.bbs.post.service.PostService;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("run")
public class InitialMemberJoinRunner implements CommandLineRunner {
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;

    @Override
    public void run(String... args) {
        JoinRequest joinRequest = new JoinRequest("react", "reactreact", "reactreact", "aa@aa.aa");
        JoinResponse member = memberService.createMember(joinRequest);
        log.debug("joined member: {}", member);

        int postNum = 15;
        for (int i = 1; i <= postNum; i++) {
            postService.createPost(
                new PostRequest("title" + i, "content" + i, "string")
                    .toServiceRequest(member.getName()));
        }
        log.debug("created {} Posts", postNum);

        int commentNum = 10;
        for (int i = 1; i <= commentNum; i++) {
            commentService.createComment(
                new CommentCreateRequest("comment-content-" + i, 15L, null)
                    .toServiceRequest(member.getName())
            );
        }
        log.debug("created {} Comments", commentNum);
    }
}

