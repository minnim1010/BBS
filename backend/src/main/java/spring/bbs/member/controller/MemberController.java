package spring.bbs.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.dto.response.JoinResponse;
import spring.bbs.member.service.MemberService;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/join")
    public ResponseEntity<JoinResponse> join(@RequestBody JoinRequest req){
        log.debug("req = {}", req);

        return ResponseEntity.ok(memberService.createMember(req));
    }

    @DeleteMapping("/members")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> withdrawal(){
        memberService.deleteMember();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
