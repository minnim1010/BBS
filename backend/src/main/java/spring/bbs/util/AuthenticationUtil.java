package spring.bbs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuthenticationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationUtil.class);

    private AuthenticationUtil() {
    }

    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOGGER.debug("No Authentication in Security Context.");
            return Optional.empty();
        }

        String username = authentication.getName();
        LOGGER.info("Security Context: Find user:{}.", authentication.getName());

        return Optional.ofNullable(username);
    }
}
