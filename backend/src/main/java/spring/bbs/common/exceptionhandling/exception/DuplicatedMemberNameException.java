package spring.bbs.common.exceptionhandling.exception;

import java.io.Serial;

public class DuplicatedMemberNameException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2L;

    public DuplicatedMemberNameException(String name) {
        super(String.format("%s: 회원 이름이 이미 존재합니다.", name));
    }
}
