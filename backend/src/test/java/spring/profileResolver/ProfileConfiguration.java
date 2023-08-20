package spring.profileResolver;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(resolver = CustomActiveProfilesResolver.class)
public @interface ProfileConfiguration {
}
