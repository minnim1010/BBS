package spring.bbs.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import spring.bbs.member.RoleType;

import java.util.List;

@SpringBootTest
public class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;
    private String testUserName = "testUser";

    @Test
    public void testGenerateAccessToken() {
        UserDetails userDetails = generateTestUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        String accessToken = jwtProvider.generateAccessToken(authentication);

        assert accessToken.isEmpty() == false;
    }

    @Test
    public void testGenerateRefreshToken() {
        UserDetails userDetails = generateTestUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        assert refreshToken.isEmpty() == false;
    }

    @Test
    public void testGetAuthentication() {
        String token = generateValidToken();
        Authentication authentication = jwtProvider.getAuthentication(token);

        assert authentication != null;
    }

    @Test
    public void testGetExpiration() {
        String token = generateValidToken();
        long expirationTime = jwtProvider.getExpiration(token);

        assert expirationTime > 0;
    }

    @Test
    public void testGetName() {
        String token = generateValidToken();
        String name = jwtProvider.getName(token);

        assert name.equals(testUserName);
    }

    @Test
    public void testIsValidToken() {
        String validToken = generateValidToken();
        String invalidToken = "invalid-token";
        boolean isValidValidToken = jwtProvider.isValidToken(validToken);
        boolean isValidInvalidToken = jwtProvider.isValidToken(invalidToken);

        assert isValidValidToken;
        assert !isValidInvalidToken;
    }

    @Test
    public void testIsLogoutToken() {
        String token = generateValidToken();
        boolean isLogoutToken = jwtProvider.isLogoutAccessToken(token);
        assert !isLogoutToken;
    }

    private String generateValidToken() {
        UserDetails userDetails = generateTestUser();
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, List.of(new SimpleGrantedAuthority(RoleType.user)));
        return jwtProvider.generateAccessToken(authentication);
    }
    
    private UserDetails generateTestUser(){
        return new User(testUserName, "password",
                List.of(new SimpleGrantedAuthority(RoleType.user)));
    }
}
