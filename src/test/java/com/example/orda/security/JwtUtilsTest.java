package com.example.orda.security;

import com.example.orda.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String jwtSecret = "testSecretKeyWithAtLeast32CharactersLongForHS256";
    private final int jwtExpirationMs = 60000;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    void generateJwtToken_ShouldReturnValidToken() {
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = new User();
        user.setUsername("testuser");
        
        when(authentication.getPrincipal()).thenReturn(user);

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("testuser", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void validateJwtToken_ShouldReturnFalseForInvalidToken() {
        String invalidToken = "this.is.not.a.valid.token";
        assertFalse(jwtUtils.validateJwtToken(invalidToken));
    }

    @Test
    void validateJwtToken_ShouldReturnFalseForExpiredToken() {
        // Set expiration to a negative value to simulate expiration
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000);
        
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = new User();
        user.setUsername("expireduser");
        when(authentication.getPrincipal()).thenReturn(user);

        String token = jwtUtils.generateJwtToken(authentication);
        
        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void getUserNameFromJwtToken_ShouldReturnCorrectUsername() {
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = new User();
        user.setUsername("extractuser");
        when(authentication.getPrincipal()).thenReturn(user);

        String token = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("extractuser", username);
    }
}