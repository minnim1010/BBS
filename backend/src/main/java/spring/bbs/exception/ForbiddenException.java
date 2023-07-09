package spring.bbs.exception;

public class ForbiddenException extends RuntimeException{
    private static final long serialVersionUID = 2L;

    public ForbiddenException(String message) {
        super(message);
    }
}
