package spring.bbs;

import org.springframework.test.context.ActiveProfiles;
import spring.profileResolver.CustomActiveProfilesResolver;

@ActiveProfiles(resolver = CustomActiveProfilesResolver.class)
public interface ProfileConfiguration {
}
