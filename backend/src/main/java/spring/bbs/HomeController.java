package spring.bbs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HomeController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/v1/no-auth")
    public String noAuth() {
        return "noAuth";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/v1/user")
    public String user() {
        return "user";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/v1/admin")
    public String admin() {
        return "admin";
    }
}
