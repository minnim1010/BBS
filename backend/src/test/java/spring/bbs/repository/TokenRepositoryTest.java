package spring.bbs.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import spring.IntegrationTestConfig;
import spring.bbs.auth.domain.AccessToken;
import spring.bbs.auth.domain.RefreshToken;
import spring.bbs.auth.domain.Token;
import spring.bbs.auth.repository.TokenRepository;

import static java.lang.Thread.sleep;


public class TokenRepositoryTest extends IntegrationTestConfig {

    private static final String MEMBER_NAME = "TokenTestUser";

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @AfterEach
    void setUp() {
        stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Nested
    @DisplayName("회원 이름을 키로 저장된 토큰이 ")
    class Exists {
        @DisplayName("존재하면 참을 반환한다.")
        @Test
        void returnTrue() {
            //given
            Token accessToken = new AccessToken(MEMBER_NAME, "AccessToken");
            Token refreshToken = new RefreshToken(MEMBER_NAME, "RefreshToken");

            stringRedisTemplate.opsForValue().set(accessToken.getKey(), accessToken.getValue());
            stringRedisTemplate.opsForValue().set(refreshToken.getKey(), refreshToken.getValue());

            //when
            boolean existsAccessToken = tokenRepository.exists(accessToken);
            boolean existsRefreshToken = tokenRepository.exists(refreshToken);

            //then
            Assertions.assertThat(existsAccessToken).isTrue();
            Assertions.assertThat(existsRefreshToken).isTrue();
        }

        @DisplayName("존재하지 않으면 거짓을 반환한다.")
        @Test
        void returnFalse() {
            //given
            Token accessToken = new AccessToken(MEMBER_NAME, "AccessToken");
            Token refreshToken = new RefreshToken(MEMBER_NAME, "RefreshToken");

            //when
            boolean existsAccessToken = tokenRepository.exists(accessToken);
            boolean existsRefreshToken = tokenRepository.exists(refreshToken);

            //then
            Assertions.assertThat(existsAccessToken).isFalse();
            Assertions.assertThat(existsRefreshToken).isFalse();
        }
    }


    @Nested
    @DisplayName("timeout을 지정해 토큰을 저장하면 ")
    class save {
        @DisplayName("timeout 시간 후 토큰이 삭제된다.")
        @Test
        void success() throws InterruptedException {
            //given
            long timeout = 2000;

            Token accessToken = new AccessToken(MEMBER_NAME, "AccessToken");
            Token refreshToken = new RefreshToken(MEMBER_NAME, "RefreshToken");

            //when
            tokenRepository.save(accessToken, timeout);
            tokenRepository.save(refreshToken, timeout);

            //then
            Assertions.assertThat(stringRedisTemplate.opsForValue().get(accessToken.getKey()))
                .isEqualTo(accessToken.getValue());
            Assertions.assertThat(stringRedisTemplate.opsForValue().get(refreshToken.getKey()))
                .isEqualTo(refreshToken.getValue());

            sleep(timeout);

            Assertions.assertThat(stringRedisTemplate.opsForValue().get(accessToken.getKey()))
                .isNull();
            Assertions.assertThat(stringRedisTemplate.opsForValue().get(refreshToken.getKey()))
                .isNull();
        }
    }

    @Nested
    @DisplayName("토큰의 키가 ")
    class Delete {
        @DisplayName("있는 경우, 삭제한다.")
        @Test
        void success() {
            //given
            Token accessToken = new AccessToken(MEMBER_NAME, "AccessToken");
            Token refreshToken = new RefreshToken(MEMBER_NAME, "RefreshToken");

            stringRedisTemplate.opsForValue().set(accessToken.getKey(), accessToken.getValue());
            stringRedisTemplate.opsForValue().set(refreshToken.getKey(), refreshToken.getValue());

            //when
            tokenRepository.delete(accessToken);
            tokenRepository.delete(refreshToken);

            //then
            Assertions.assertThat(stringRedisTemplate.opsForValue().get(accessToken.getKey()))
                .isNull();
            Assertions.assertThat(stringRedisTemplate.opsForValue().get(refreshToken.getKey()))
                .isNull();
        }

        @DisplayName("없는 경우, 삭제하지 않는다.")
        @Test
        void failButOk() {
            //given
            Token accessToken = new AccessToken(MEMBER_NAME, "AccessToken");
            Token refreshToken = new RefreshToken(MEMBER_NAME, "RefreshToken");

            //when
            tokenRepository.delete(accessToken);
            tokenRepository.delete(refreshToken);

            //then
            Assertions.assertThat(stringRedisTemplate.opsForValue().get(accessToken.getKey()))
                .isNull();
            Assertions.assertThat(stringRedisTemplate.opsForValue().get(refreshToken.getKey()))
                .isNull();
        }
    }
}
