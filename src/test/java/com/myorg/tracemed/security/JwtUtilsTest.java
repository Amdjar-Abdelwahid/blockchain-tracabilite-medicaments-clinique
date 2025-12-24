package com.myorg.tracemed.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void generateTokenShouldCreateValidToken() {
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        String token = jwtUtils.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void extractUsernameShouldReturnCorrectUsername() {
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        String token = jwtUtils.generateToken(userDetails);
        String username = jwtUtils.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void isTokenValidShouldReturnTrueForValidToken() {
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        String token = jwtUtils.generateToken(userDetails);
        boolean valid = jwtUtils.isTokenValid(token, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValidShouldReturnFalseForDifferentUser() {
        UserDetails user1 = User.withUsername("user1")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        UserDetails user2 = User.withUsername("user2")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        String token = jwtUtils.generateToken(user1);
        boolean valid = jwtUtils.isTokenValid(token, user2);

        assertThat(valid).isFalse();
    }
}
