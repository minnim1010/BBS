package spring.bbs.member.dto.util;

import spring.bbs.member.domain.Authority;
import spring.bbs.member.domain.Member;
import spring.bbs.member.dto.request.JoinRequest;

public class RequestToMember {
    public static Member convertJoinRequestToMember(JoinRequest req, String authorityType, String encodedPassword){
        return new Member(req.getName(), encodedPassword, req.getEmail(),
                true, Enum.valueOf(Authority.class, authorityType));
    }
}
