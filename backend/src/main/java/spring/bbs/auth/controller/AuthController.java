package spring.bbs.auth.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import spring.bbs.auth.controller.dto.request.CreateAccessTokenRequest;
import spring.bbs.auth.controller.dto.request.LoginRequest;
import spring.bbs.auth.controller.dto.response.AccessTokenResponse;
import spring.bbs.auth.controller.dto.response.LoginResponse;
import spring.bbs.auth.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService jwtService;

    public AuthController(AuthService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return jwtService.login(req);
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public AccessTokenResponse sendNewAccessToken(@RequestBody @Valid CreateAccessTokenRequest req) {
        return jwtService.createNewAccessToken(req);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestHeader("Authorization") String tokenHeaderValue) {
        jwtService.logout(tokenHeaderValue.substring(7));
    }
}