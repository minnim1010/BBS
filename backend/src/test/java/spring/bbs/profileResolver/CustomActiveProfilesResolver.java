package spring.bbs.profileResolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.ActiveProfilesResolver;

@Slf4j
public class CustomActiveProfilesResolver implements ActiveProfilesResolver {
    private final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    @Override
    public String[] resolve(Class<?> testClass) {
        String property = System.getProperty(SPRING_PROFILES_ACTIVE);
        log.debug("Active Profile: {}", property);
        return new String[]{property};
    }
}
