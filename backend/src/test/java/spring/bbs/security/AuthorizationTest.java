package spring.bbs.security;


import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import spring.bbs.common.jwt.JwtProvider;
import spring.helper.AccessTokenProvider;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthorizationTest {

    private static final String MEMBER_NAME = "AuthorizationTestUser";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private JwtProvider jwtProvider;

    private AccessTokenProvider accessTokenProvider;

    @PostConstruct
    void init() {
        accessTokenProvider = new AccessTokenProvider(jwtProvider, MEMBER_NAME);
    }

    @Nested
    class NoAuth {
        private static final String url = "/home";

        @Test
        @DisplayName(url + " 요청 시 로그인하지 않아도 요청을 처리한다.")
        void homeWithNoAuth() throws Exception {
            mockMvc.perform(get(url))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName(url + " 요청 시 사용자 권한인 경우 요청을 처리한다.")
        void homeWithUserAuth() throws Exception {
            String AuthHeaderValue = getUserRoleTokenWithHeaderPrefix();

            mockMvc.perform(get(url)
                    .header(accessTokenProvider.AUTHENTICATION_HEADER, AuthHeaderValue))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName(url + " 요청 시 관리자 권한인 경우 요청을 처리한다.")
        void homeWithAdminAuth() throws Exception {
            String AuthHeaderValue = getAdminRoleTokenWithHeaderPrefix();

            mockMvc.perform(get(url)
                    .header(accessTokenProvider.AUTHENTICATION_HEADER, AuthHeaderValue))
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
            String AuthHeaderValue = getUserRoleTokenWithHeaderPrefix();
            mockMvc.perform(get(url)
                    .header(accessTokenProvider.AUTHENTICATION_HEADER, AuthHeaderValue))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName(url + " 요청 시 관리자 권한인 경우 요청을 처리한다.")
        void userWithAdminAuth() throws Exception {
            String AuthHeaderValue = getAdminRoleTokenWithHeaderPrefix();
            mockMvc.perform(get(url)
                    .header(accessTokenProvider.AUTHENTICATION_HEADER, AuthHeaderValue))
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
            String AuthHeaderValue = getUserRoleTokenWithHeaderPrefix();
            mockMvc.perform(get(url)
                    .header(accessTokenProvider.AUTHENTICATION_HEADER, AuthHeaderValue))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName(url + " 요청 시 관리자 권한인 경우 요청을 처리한다.")
        void adminWithAdminAuth() throws Exception {
            String AuthHeaderValue = getAdminRoleTokenWithHeaderPrefix();
            mockMvc.perform(get(url)
                    .header(accessTokenProvider.AUTHENTICATION_HEADER, AuthHeaderValue))
                .andExpect(status().isOk());
        }
    }

    private String getUserRoleTokenWithHeaderPrefix() {
        return accessTokenProvider.getUserRoleTokenWithHeaderPrefix();
    }

    private String getAdminRoleTokenWithHeaderPrefix() {
        return accessTokenProvider.getAdminRoleTokenWithHeaderPrefix();
    }
}
