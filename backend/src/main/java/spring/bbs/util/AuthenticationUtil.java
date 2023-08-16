package spring.bbs.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class AuthenticationUtil {

    private AuthenticationUtil() {
    }

    private static Optional<String> getCurrentMemberName() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.debug("로그인된 회원이 없습니다.");
            return Optional.empty();
        }

        String username = authentication.getName();
        log.info("현재 로그인된 유저: {}", authentication.getName());

        return Optional.ofNullable(username);
    }

    public static String getCurrentMemberNameOrAccessDenied(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new AccessDeniedException("회원만 이용할 수 있습니다.");

        return authentication.getName();
    }
}
