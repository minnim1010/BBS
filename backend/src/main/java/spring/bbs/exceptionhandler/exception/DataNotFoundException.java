package spring.bbs.exceptionhandler.exception;

public class DataNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 3L;

    public DataNotFoundException(String message) {
        super(message);
    }
}
