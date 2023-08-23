package spring.bbs.auth.controller.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessTokenResponse {
    private String token;

    public AccessTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
