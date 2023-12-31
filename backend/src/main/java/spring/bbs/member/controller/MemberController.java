package spring.bbs.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spring.bbs.auth.controller.dto.response.UserInfoResponse;
import spring.bbs.common.constant.Api;
import spring.bbs.member.controller.dto.JoinRequest;
import spring.bbs.member.controller.dto.JoinResponse;
import spring.bbs.member.domain.Member;
import spring.bbs.member.service.MemberService;


@Slf4j
@RestController
@RequestMapping(Api.Domain.MEMBER)
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/auth-info")
    @ResponseStatus(HttpStatus.OK)
    public UserInfoResponse getMember(@AuthenticationPrincipal Member currentMember) {
        return memberService.get(currentMember.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public JoinResponse createMember(@RequestBody @Valid JoinRequest req) {
        return memberService.createMember(req);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteMember(@AuthenticationPrincipal Member member) {
        memberService.deleteMember(member.getName());
    }
}
