package spring.bbs.common.exceptionhandling.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ExceptionResponse {
    private String description;

    private String errorMessage;

    public ExceptionResponse(String description, String errorMessage) {
        this.description = description;
        this.errorMessage = errorMessage;
    }
}
