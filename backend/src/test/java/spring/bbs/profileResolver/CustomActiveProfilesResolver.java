package spring.bbs.profileResolver;

import org.springframework.test.context.ActiveProfilesResolver;

public class CustomActiveProfilesResolver implements ActiveProfilesResolver {
    private final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    @Override
    public String[] resolve(Class<?> testClass) {
        String property = System.getProperty(SPRING_PROFILES_ACTIVE);
        System.out.println(property);
        return new String[]{property};
    }
}
