package spring.bbs.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import spring.bbs.exceptionhandler.exception.DataNotFoundException;
import spring.bbs.exceptionhandler.exception.ExistedMemberNameException;
import spring.bbs.exceptionhandler.exception.NotSamePasswordException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = {
        "spring.bbs.jwt.controller",
        "spring.bbs.written.comment.controller",
        "spring.bbs.member.controller",
        "spring.bbs.written.post.controller"})
public class ExceptionHandler {

    private ExceptionResponse createExceptionResponse(String errorMsg){
        return new ExceptionResponse("An error occurred", errorMsg);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleDataNotFoundException(Exception ex) {
        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(Exception ex) {
        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException(Exception ex) {
        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ExistedMemberNameException.class)
    public ResponseEntity<ExceptionResponse> handleExistedUserNameException(Exception ex) {
        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotSamePasswordException.class)
    public ResponseEntity<ExceptionResponse> handleNotSamePasswordException(Exception ex) {
        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach((error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        return ResponseEntity.badRequest().body(errors);
    }

//    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
//        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());
//
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
