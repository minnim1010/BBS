package spring.bbs.auth.domain;

import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class AccessToken extends Token {
    public final String ACCESS_TOKEN_KEY_PREFIX = "access:";
    
    public AccessToken(String key, String token) {
        Assert.hasText(key, "key는 공백일 수 없습니다.");

        this.key = ACCESS_TOKEN_KEY_PREFIX + key;
        this.token = token;
    }

    public AccessToken(String key) {
        Assert.hasText(key, "key는 공백일 수 없습니다.");

        this.key = ACCESS_TOKEN_KEY_PREFIX + key;
    }
}
