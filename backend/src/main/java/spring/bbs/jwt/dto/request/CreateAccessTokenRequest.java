package spring.bbs.jwt.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAccessTokenRequest {
    String refreshToken;

    public CreateAccessTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
