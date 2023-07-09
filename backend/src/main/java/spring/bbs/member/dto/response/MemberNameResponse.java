package spring.bbs.member.dto.response;

public class MemberNameResponse {
    private String name;

    public MemberNameResponse() {
    }

    public MemberNameResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
