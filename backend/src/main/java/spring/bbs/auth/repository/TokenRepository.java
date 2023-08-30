package spring.bbs.auth.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import spring.bbs.auth.domain.Token;

import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Component
public class TokenRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public boolean exists(Token token) {
        String key = token.getKey();
        String value = token.getToken();

        String resultToken = stringRedisTemplate.opsForValue().get(key);

        if (value == null) {
            return resultToken != null;
        }

        return value.equals(resultToken);
    }

    public Token save(Token token, long timeout) {
        stringRedisTemplate.opsForValue().set(
            token.getKey(), token.getToken(), timeout, TimeUnit.MILLISECONDS);

        return token;
    }

    public void delete(Token token) {
        Boolean result = stringRedisTemplate.delete(token.getKey());

        if (result == null || !result) {
            log.info("{}: did not exist.", token.getKey());
        }
    }
}
