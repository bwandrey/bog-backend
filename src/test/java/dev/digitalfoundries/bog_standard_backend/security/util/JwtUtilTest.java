package dev.digitalfoundries.bog_standard_backend.security.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String secret = "MySuperSecretKeyThatIsLongEnoughToUse1234567890";
    private final long expiration = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inject fields via reflection because @Value doesn't work outside Spring context
        ReflectionTestUtils.setField(jwtUtil, "secret", java.util.Base64.getEncoder().encodeToString(secret.getBytes()));
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
    }

    @Test
    void generateTokenAndValidate() {
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", List.of());

        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertEquals(username, jwtUtil.getUsernameFromToken(token));
        assertFalse(jwtUtil.isTokenExpired(token));
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_shouldFailIfUsernameDiffers() {
        String token = jwtUtil.generateToken("testuser");

        UserDetails differentUser = new User("anotheruser", "pass", List.of());

        assertFalse(jwtUtil.validateToken(token, differentUser));
    }

    @Test
    void getExpirationDateFromToken_shouldBeInFuture() {
        String token = jwtUtil.generateToken("testuser");
        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);

        assertTrue(expirationDate.after(new Date()));
    }
}
