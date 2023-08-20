package spring.bbs.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import spring.bbs.common.util.RoleType;
import spring.bbs.jwt.JwtProvider;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
public class JwtProviderTest {

    private static final String MEMBER_NAME = "JwtTestUser";
    @Autowired
    private JwtProvider jwtProvider;


    @Test
    public void testGenerateAccessToken() {
        UserDetails userDetails = generateTestUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        String accessToken = jwtProvider.generateAccessToken(authentication);

        assertThat(accessToken).isNotEmpty();
    }

    @Test
    public void testGenerateRefreshToken() {
        UserDetails userDetails = generateTestUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        assertThat(refreshToken).isNotEmpty();
    }

    @Test
    public void testGetAuthentication() {
        String token = generateValidToken();
        Authentication authentication = jwtProvider.getAuthentication(token);

        assertThat(authentication).isNotNull();
    }

    @Test
    public void testGetExpiration() {
        String token = generateValidToken();
        long expirationTime = jwtProvider.getExpiration(token);

        assertThat(expirationTime).isGreaterThan(0);
    }

    @Test
    public void testGetName() {
        String token = generateValidToken();
        String name = jwtProvider.getName(token);

        assertThat(name).isEqualTo(MEMBER_NAME);
    }

    @Test
    public void testIsValidToken() {
        String validToken = generateValidToken();
        String invalidToken = "invalid-token";
        boolean isValidToken = jwtProvider.isValidToken(validToken);
        assertThat(isValidToken).isTrue();

        isValidToken = jwtProvider.isValidToken(invalidToken);
        assertThat(isValidToken).isFalse();
    }

    @Test
    public void testIsLogoutToken() {
        String token = generateValidToken();
        boolean isLogoutToken = jwtProvider.isLogoutAccessToken(token);
        assertThat(isLogoutToken).isFalse();
    }

    private String generateValidToken() {
        UserDetails userDetails = generateTestUser();
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, List.of(new SimpleGrantedAuthority(RoleType.user)));
        return jwtProvider.generateAccessToken(authentication);
    }

    private UserDetails generateTestUser() {
        return new User(MEMBER_NAME, "password",
            List.of(new SimpleGrantedAuthority(RoleType.user)));
    }
}
