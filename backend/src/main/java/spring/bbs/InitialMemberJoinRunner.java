package spring.bbs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import spring.bbs.member.dto.request.JoinRequest;
import spring.bbs.member.dto.response.JoinResponse;
import spring.bbs.member.service.MemberService;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitialMemberJoinRunner implements CommandLineRunner {
    private final MemberService memberService;

    @Override
    public void run(String... args) {
        JoinRequest joinRequest = new JoinRequest("react", "reactreact", "reactreact", "aa@aa.aa");
        JoinResponse member = memberService.createMember(joinRequest);
        log.debug("joined member: {}", member);
    }
}
