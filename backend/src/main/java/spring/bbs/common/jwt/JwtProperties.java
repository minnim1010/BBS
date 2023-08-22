package spring.bbs.common.jwt;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.Duration;

@ConfigurationProperties("jwt")
@Getter
public class JwtProperties {
    private final String header;
    private final String secret;
    private final Duration accessTokenDuration;
    private final Duration refreshTokenDuration;

    @ConstructorBinding
    public JwtProperties(String header, String secret, Duration accessTokenDuration, Duration refreshTokenDuration) {
        this.header = header;
        this.secret = secret;
        this.accessTokenDuration = accessTokenDuration;
        this.refreshTokenDuration = refreshTokenDuration;
    }
}
