package spring.bbs.security;


import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import spring.IntegrationTestConfig;
import spring.bbs.auth.controller.dto.response.LoginResponse;
import spring.bbs.common.jwt.JwtProperties;
import spring.bbs.common.jwt.JwtProvider;
import spring.helper.TestTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthorizationTest extends IntegrationTestConfig {

    private static final String MEMBER_NAME = "AuthorizationTestUser";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JwtProperties jwtProperties;

    private TestTokenProvider testTokenProvider;

    @PostConstruct
    void init() {
        testTokenProvider = new TestTokenProvider(jwtProvider);
    }

    @DisplayName("유효하지 않은 accessToken과 유효한 refreshToken이 있다면, 새로운 accessToken을 만들고 요청을 처리한다.")
    @Test
    void successWithInvalidAccessTokenAndValidRefreshToken() throws Exception {
        //given
        Cookie[] userRoleTokenCookie = getUserRoleTokenCookie();
        userRoleTokenCookie[0].setValue("invalidAccessToken");

        //when
        ResultActions perform = mockMvc.perform(get(UserAuth.url)
            .cookie(userRoleTokenCookie));

        // then
        MvcResult mvcResult = perform.andExpect(status().isOk())
            .andExpect(cookie().exists(jwtProperties.getAccessTokenCookieName()))
            .andReturn();
        String accessToken =
            mvcResult.getResponse().getCookie(jwtProperties.getAccessTokenCookieName()).getValue();
        assertThat(jwtProvider.isValidToken(accessToken)).isTrue();
    }

    private Cookie[] getUserRoleTokenCookie() {
        LoginResponse token = testTokenProvider.getTokenWithUserRole(MEMBER_NAME);
        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie(jwtProperties.getAccessTokenCookieName(), token.getAccessToken());
        cookies[1] = new Cookie(jwtProperties.getRefreshTokenCookieName(), token.getRefreshToken());

        return cookies;
    }

    private Cookie[] getAdminRoleTokenCookie() {
        LoginResponse token = testTokenProvider.getTokenWithAdminRole(MEMBER_NAME);
        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie(jwtProperties.getAccessTokenCookieName(), token.getAccessToken());
        cookies[1] = new Cookie(jwtProperties.getRefreshTokenCookieName(), token.getRefreshToken());

        return cookies;
    }

    @Nested
    class NoAuth {
        private static final String url = "/api/v1/no-auth";

        @Test
        @DisplayName(url + " 요청 시 로그인하지 않아도 요청을 처리한다.")
        void successWithNoAuth() throws Exception {
            mockMvc.perform(get(url))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName(url + " 요청 시 사용자 권한인 경우 요청을 처리한다.")
        void successWithUserAuth() throws Exception {
            mockMvc.perform(get(url)
                    .cookie(getUserRoleTokenCookie()))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName(url + " 요청 시 관리자 권한인 경우 요청을 처리한다.")
        void successWithAdminAuth() throws Exception {
            mockMvc.perform(get(url)
                    .cookie(getAdminRoleTokenCookie()))
                .andExpect(status().isOk());
        }
    }

    @Nested
    class UserAuth {
        private static final String url = "/api/v1/user";

        @Test
        @DisplayName(url + " 요청 시 권한이 없다면 요청을 처리하지 않는다.")
        void userWithNoAuth() throws Exception {
            mockMvc.perform(get(url))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName(url + " 요청 시 사용자 권한인 경우 요청을 처리한다.")
        void userWithUserAuth() throws Exception {
            mockMvc.perform(get(url)
                    .cookie(getUserRoleTokenCookie()))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName(url + " 요청 시 관리자 권한인 경우 요청을 처리한다.")
        void userWithAdminAuth() throws Exception {
            mockMvc.perform(get(url)
                    .cookie(getAdminRoleTokenCookie()))
                .andExpect(status().isOk());
        }
    }

    @Nested
    class AdminAuth {
        private static final String url = "/api/v1/admin";

        @Test
        @DisplayName(url + " 요청 시 로그인하지 않으면 요청을 처리하지 않는다.")
        void adminWithNoAuth() throws Exception {
            mockMvc.perform(get(url))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName(url + " 요청 시 사용자 권한인 경우 요청을 처리하지 않는다.")
        void adminWithUserAuth() throws Exception {
            mockMvc.perform(get(url)
                    .cookie(getUserRoleTokenCookie()))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName(url + " 요청 시 관리자 권한인 경우 요청을 처리한다.")
        void adminWithAdminAuth() throws Exception {
            mockMvc.perform(get(url)
                    .cookie(getAdminRoleTokenCookie()))
                .andExpect(status().isOk());
        }
    }
}
