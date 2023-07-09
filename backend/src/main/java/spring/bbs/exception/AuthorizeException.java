package spring.bbs.exception;

public class AuthorizeException extends RuntimeException{
    private static final long serialVersionUID = 3L;

    public AuthorizeException(String message) {
        super(message);
    }
}
