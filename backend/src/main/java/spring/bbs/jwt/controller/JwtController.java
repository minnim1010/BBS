package spring.bbs.jwt.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.bbs.jwt.JwtFilter;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.LoginResponse;
import spring.bbs.jwt.service.JwtService;

@RestController
@RequestMapping("/api/v1")
public class JwtController {

    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResponse response = jwtService.login(req);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + response.getToken());

        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void logout(){
    }
}
