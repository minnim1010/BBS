package spring.bbs.common.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ConfigurationProperties("jwt")
@Getter
@Setter
@Component
public class JwtProperties {
    private String header;
    private String secret;
    private Duration accessTokenDuration;
    private Duration refreshTokenDuration;

    @Override
    public String toString() {
        return "JwtProperties{" +
            "header='" + header + '\'' +
            ", secret='" + secret + '\'' +
            ", accessTokenValidMilSeconds=" + accessTokenDuration +
            ", refreshTokenValidMilSeconds=" + refreshTokenDuration +
            '}';
    }
}
