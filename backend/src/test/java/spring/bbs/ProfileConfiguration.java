package spring.bbs;

import org.springframework.test.context.ActiveProfiles;

//@ActiveProfiles(resolver = CustomActiveProfilesResolver.class)
@ActiveProfiles("local")
public interface ProfileConfiguration {
}
