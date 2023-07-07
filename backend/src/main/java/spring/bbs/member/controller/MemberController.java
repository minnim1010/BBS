package spring.bbs.member.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spring.bbs.member.domain.Member;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.dto.response.JoinResponse;
import spring.bbs.member.service.MemberService;

@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private final Logger logger = LoggerFactory.getLogger(
            MemberController.class);

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/join")
    public ResponseEntity<JoinResponse> join(JoinRequest req){
        return ResponseEntity.ok(memberService.createMember(req));
    }

    @DeleteMapping("/withdrawal")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> withdrawal(@AuthenticationPrincipal Member member){
        logger.debug("MemberController.withdrawal");
        logger.debug("{}", member);
        memberService.deleteMember(member.getName());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
