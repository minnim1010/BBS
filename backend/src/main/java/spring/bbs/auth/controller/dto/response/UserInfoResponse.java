package spring.bbs.auth.controller.dto.response;

import lombok.Getter;
import spring.bbs.member.domain.Member;

@Getter
public class UserInfoResponse {
    private final String username;
    private final String email;

    private UserInfoResponse(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public static UserInfoResponse of(Member member) {
        return new UserInfoResponse(member.getName(), member.getEmail());
    }
}
