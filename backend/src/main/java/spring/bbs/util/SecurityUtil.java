package spring.bbs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import spring.bbs.member.domain.Member;

import java.util.Optional;

public class SecurityUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtil.class);

    private SecurityUtil() {
    }

    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOGGER.debug("No Authentication in Security Context.");
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof Member) {
            Member loginedMember = (Member) authentication.getPrincipal();
            username = loginedMember.getName();
        } else if (authentication.getPrincipal() instanceof String)
            username = (String) authentication.getPrincipal();

        LOGGER.info("Security Context: Find user:{}.", username);

        return Optional.ofNullable(username);
    }
}
