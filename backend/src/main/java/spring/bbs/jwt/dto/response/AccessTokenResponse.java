package spring.bbs.jwt.dto.response;

public class AccessTokenResponse {
    private String token;

    public AccessTokenResponse() {
    }

    public AccessTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
