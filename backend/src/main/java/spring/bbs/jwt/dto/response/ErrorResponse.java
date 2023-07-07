package spring.bbs.jwt.dto.response;

public class ErrorResponse {
    private String description;
    private String errorMessage;

    public ErrorResponse() {
    }

    public ErrorResponse(String description, String errorMessage) {
        this.description = description;
        this.errorMessage = errorMessage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
