package spring.bbs.common.exception;

public class ExistedMemberNameException extends RuntimeException {
    private static final long serialVersionUID = 2L;

    public ExistedMemberNameException() {
        super("회원 이름이 이미 존재합니다.");
    }
}
