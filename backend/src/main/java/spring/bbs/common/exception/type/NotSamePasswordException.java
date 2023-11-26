package spring.bbs.common.exception.type;

import java.io.Serial;

public class NotSamePasswordException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3L;

    public NotSamePasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
