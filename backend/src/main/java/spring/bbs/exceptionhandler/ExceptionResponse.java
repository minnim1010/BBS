package spring.bbs.exceptionhandler;

public class ExceptionResponse {
    private String description;

    private String errorMessage;

    public ExceptionResponse() {
    }

    public ExceptionResponse(String description, String errorMessage) {
        this.description = description;
        this.errorMessage = errorMessage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
