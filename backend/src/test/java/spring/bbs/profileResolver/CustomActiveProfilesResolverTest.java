package spring.bbs.profileResolver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles(resolver = CustomActiveProfilesResolver.class)
class CustomActiveProfilesResolverTest {
    @Value("${spring.config.activate.on-profile}")
    private String profile;

    @Test
    void ActiveProfilesResolver_이용하여_local인지_확인한다(){
        System.out.println("profile: " + profile);
        assertEquals(profile, "local");
    }
}