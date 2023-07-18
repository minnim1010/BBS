package spring.bbs.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.exceptionhandler.exception.ExistedMemberNameException;
import spring.bbs.exceptionhandler.exception.NotSamePasswordException;

@ControllerAdvice(basePackages = {
        "spring.bbs.jwt.controller",
        "spring.bbs.comment.controller",
        "spring.bbs.member.controller",
        "spring.bbs.post.controller"})
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleDataNotFoundException(Exception ex) {
        ExceptionResponse errorResponse = new ExceptionResponse("An error occurred", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(Exception ex) {
        ExceptionResponse errorResponse = new ExceptionResponse("An error occurred", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException(Exception ex) {
        ExceptionResponse errorResponse = new ExceptionResponse("An error occurred", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ExistedMemberNameException.class)
    public ResponseEntity<ExceptionResponse> handleExistedUserNameException(Exception ex) {
        ExceptionResponse errorResponse = new ExceptionResponse("An error occurred", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotSamePasswordException.class)
    public ResponseEntity<ExceptionResponse> handleNotSamePasswordException(Exception ex) {
        ExceptionResponse errorResponse = new ExceptionResponse("An error occurred", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        ExceptionResponse errorResponse = new ExceptionResponse("An error occurred", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
