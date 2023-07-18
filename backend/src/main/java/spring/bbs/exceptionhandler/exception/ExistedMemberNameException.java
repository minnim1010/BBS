package spring.bbs.exceptionhandler.exception;

public class ExistedMemberNameException extends RuntimeException{
    private static final long serialVersionUID = 2L;

    public ExistedMemberNameException(String message) {
        super(message);
    }
}
