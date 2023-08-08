package spring.bbs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/error")
    @ResponseBody
    public ResponseEntity<String> error(){
        return new ResponseEntity<>("Error occured.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/social-login")
    public String socialLogin() {

        return "social_login";
    }
}
