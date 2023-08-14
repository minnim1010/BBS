package spring.bbs;

import org.springframework.test.context.ActiveProfiles;
import profileResolver.CustomActiveProfilesResolver;

@ActiveProfiles(resolver = CustomActiveProfilesResolver.class)
public interface ProfileConfiguration {
}
