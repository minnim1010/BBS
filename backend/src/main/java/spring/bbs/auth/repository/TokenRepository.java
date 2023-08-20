package spring.bbs.auth.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Component
public class TokenRepository {
    private final String ACCESS_TOKEN_VALUE = "access";

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean existsByAccessToken(String token) {
        return (String) redisTemplate.opsForValue().get(token) != null;
    }

    public boolean existsRefreshTokenByName(String name) {
        return (String) redisTemplate.opsForValue().get(name) != null;
    }

    public void saveAccessToken(String key, long timeout) {
        redisTemplate.opsForValue().set(key, ACCESS_TOKEN_VALUE, timeout, TimeUnit.MILLISECONDS);
    }

    public void saveRefreshToken(String key, String token, long timeout) {
        redisTemplate.opsForValue().set(key, token, timeout, TimeUnit.MILLISECONDS);
    }

    public void deleteRefreshToken(String key) {
        Boolean result = redisTemplate.delete(key);
        if (result == null || !result) {
            log.warn("{}: Cannot remove refresh token", key);
        }
    }
}
