package spring.bbs.common.exceptionhandling.exception;

public class NotSamePasswordException extends RuntimeException {
    private static final long serialVersionUID = 3L;

    public NotSamePasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
