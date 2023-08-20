package spring.bbs.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class AuthenticationUtil {

    private AuthenticationUtil() {
    }
    

    public static String getCurrentMemberNameOrAccessDenied() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("회원만 이용할 수 있습니다.");
        }

        return authentication.getName();
    }
}
