package spring.bbs.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.LoginResponse;
import spring.bbs.jwt.service.JwtService;

@Controller
@RequestMapping("/api/v1")
public class JwtController {

    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResponse response = jwtService.login(req);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public String logout(HttpServletRequest request) {
        jwtService.logout(request.getHeader("Authorization").substring(7));
        return "redirect:/home";
    }
}
