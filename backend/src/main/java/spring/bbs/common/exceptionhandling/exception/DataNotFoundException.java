package spring.bbs.common.exceptionhandling.exception;

import java.io.Serial;

public class DataNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DataNotFoundException(String key) {
        super(String.format("%s: 해당 키를 사용하여 데이터를 찾을 수 없습니다.", key));
    }
}
