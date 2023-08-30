package spring.bbs.auth.domain;

import org.springframework.util.Assert;

public class RefreshToken extends Token {
    public final String KEY_PREFIX = "refresh:";

    public RefreshToken(String key, String token) {
        Assert.hasText(key, "key는 공백일 수 없습니다.");

        this.key = KEY_PREFIX + key;
        this.token = token;
    }

    public RefreshToken(String key) {
        Assert.hasText(key, "key는 공백일 수 없습니다.");

        this.key = KEY_PREFIX + key;
    }
}
