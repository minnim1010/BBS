package spring.profileResolver;

import org.springframework.test.context.ActiveProfilesResolver;

public class CustomActiveProfilesResolver implements ActiveProfilesResolver {
    private final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    @Override
    public String[] resolve(Class<?> testClass) {
        String property = System.getProperty(SPRING_PROFILES_ACTIVE);
        return new String[]{property};
    }
}
