package spring.bbs.member.dto.util;

import spring.bbs.member.domain.Member;
import spring.bbs.member.dto.response.JoinResponse;

public class MemberToResponse {
    public static JoinResponse convertMemberToResponse(Member member){
        return new JoinResponse(member.getId(), member.getName(), member.getEmail());
    }
}
