package spring.bbs.jwt.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenRepository {
    private final String REFRESH_TOKEN_VALUE = "refresh";
    private final String ACCESS_TOKEN_VALUE = "access";

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean existsAccessToken(String token){
        String result = (String) redisTemplate.opsForValue().get(token);
        if(result == null) return false;
        return result.equals(ACCESS_TOKEN_VALUE);
    }

    public void saveAccessToken(String key, long timeout){
        redisTemplate.opsForValue().set(key, ACCESS_TOKEN_VALUE, timeout, TimeUnit.MILLISECONDS);
    }
}
