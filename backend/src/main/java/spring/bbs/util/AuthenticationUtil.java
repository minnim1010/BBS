package spring.bbs.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class AuthenticationUtil {

    private AuthenticationUtil() {
    }

    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.debug("No Authentication in Security Context.");
            return Optional.empty();
        }

        String username = authentication.getName();
        log.info("Security Context: Find user:{}.", authentication.getName());

        return Optional.ofNullable(username);
    }
}
