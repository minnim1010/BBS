package spring.bbs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/api/v1/user")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @ResponseBody
    public void user() {
    }

    @GetMapping("/api/v1/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public void admin() {
    }

    @GetMapping("/social-login")
    public String socialLogin() {

        return "social_login";
    }
}
