package spring.bbs.exceptionhandler.exception;

public class TokenCannotSaveException extends RuntimeException{
    private static final long serialVersionUID = 4L;

    public TokenCannotSaveException(String message) {
        super(message);
    }
}
