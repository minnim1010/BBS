package spring.bbs.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spring.bbs.member.domain.Member;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.dto.response.JoinResponse;
import spring.bbs.member.service.MemberService;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public JoinResponse createMember(
        @RequestBody @Valid
        JoinRequest req
    ) {
        return memberService.createMember(req);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteMember(
        @AuthenticationPrincipal
        Member member
    ) {
        memberService.deleteMember(member.getName());
    }
}
