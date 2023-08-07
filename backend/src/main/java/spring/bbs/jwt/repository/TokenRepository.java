package spring.bbs.jwt.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TokenRepository {
    private final String REFRESH_TOKEN_VALUE = "refresh";
    private final String ACCESS_TOKEN_VALUE = "access";

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean existsRefreshToken(String token){
        String refresh = (String) redisTemplate.opsForValue().get(token);
        if(refresh == null) return false;
        return refresh.equals(REFRESH_TOKEN_VALUE);
    }

    public boolean existsAccessToken(String token){
        String access = (String) redisTemplate.opsForValue().get(token);
        if(access == null) return false;
        return access.equals(ACCESS_TOKEN_VALUE);
    }

    public void saveRefreshToken(String key, long timeout){
        redisTemplate.opsForValue().set(key, REFRESH_TOKEN_VALUE, timeout, TimeUnit.SECONDS);
    }

    public void saveAccessToken(String key, long timeout){
        redisTemplate.opsForValue().set(key, ACCESS_TOKEN_VALUE, timeout, TimeUnit.SECONDS);
    }
}
