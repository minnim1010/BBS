package spring.bbs.common.exceptionhandling.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import spring.bbs.common.exceptionhandling.exception.DataNotFoundException;
import spring.bbs.common.exceptionhandling.exception.ExistedMemberNameException;
import spring.bbs.common.exceptionhandling.exception.NotSamePasswordException;
import spring.bbs.common.exceptionhandling.response.ExceptionResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = {
    "spring.bbs.jwt.controller",
    "spring.bbs.comment.controller",
    "spring.bbs.member.controller",
    "spring.bbs.post.controller"})
public class ExceptionHandlerAdvice {

    private ExceptionResponse createExceptionResponse(String errorMsg) {
        return new ExceptionResponse("An error occurred", errorMsg);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleDataNotFoundException(Exception ex) {
        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(Exception ex) {
        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException(Exception ex) {
        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExistedMemberNameException.class)
    public ResponseEntity<ExceptionResponse> handleExistedUserNameException(Exception ex) {
        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotSamePasswordException.class)
    public ResponseEntity<ExceptionResponse> handleNotSamePasswordException(Exception ex) {
        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
            .forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        return ResponseEntity.badRequest().body(errors);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
//        ExceptionResponse errorResponse = createExceptionResponse(ex.getMessage());
//
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
