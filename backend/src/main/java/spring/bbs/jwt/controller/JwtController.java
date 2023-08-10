package spring.bbs.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.bbs.jwt.dto.request.CreateAccessTokenRequest;
import spring.bbs.jwt.dto.request.LoginRequest;
import spring.bbs.jwt.dto.response.AccessTokenResponse;
import spring.bbs.jwt.dto.response.LoginResponse;
import spring.bbs.jwt.service.JwtService;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class JwtController {

    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        LoginResponse response = jwtService.login(req);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> sendNewAccessToken(@RequestBody @Valid CreateAccessTokenRequest req) {
        AccessTokenResponse response = jwtService.createNewAccessToken(req);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        jwtService.logout(request.getHeader("Authorization").substring(7));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
