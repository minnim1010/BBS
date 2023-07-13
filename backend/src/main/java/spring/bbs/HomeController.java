package spring.bbs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/error")
    public ResponseEntity<String> error(){
        return new ResponseEntity<>("Error occured.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
