package spring.bbs.exceptionhandler.exception;

public class NotSamePasswordException extends RuntimeException{
    private static final long serialVersionUID = 3L;

    public NotSamePasswordException(String message) {
        super(message);
    }
}
