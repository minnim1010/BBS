package spring.bbs.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import spring.bbs.auth.controller.dto.request.LoginRequest;
import spring.bbs.auth.controller.dto.response.LoginResponse;
import spring.bbs.auth.service.AuthService;
import spring.bbs.common.jwt.JwtProperties;
import spring.bbs.common.util.CookieUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService jwtService;
    private final JwtProperties jwtProperties;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(HttpServletResponse response, @RequestBody @Valid LoginRequest req) {
        LoginResponse token = jwtService.login(req);
        CookieUtil.addCookie(response, jwtProperties.getAccessTokenCookieName(), token.getAccessToken(),
            jwtProperties.getAccessTokenDuration().getSeconds());
        CookieUtil.addCookie(response, jwtProperties.getRefreshTokenCookieName(), token.getRefreshToken(),
            jwtProperties.getRefreshTokenDuration().getSeconds());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request) {
        String accessToken =
            CookieUtil.getCookieValue(request, jwtProperties.getAccessTokenCookieName())
                .orElseThrow(() -> new IllegalStateException("쿠키에서 access token을 가져올 수 없습니다."));
        jwtService.logout(accessToken);
    }
}