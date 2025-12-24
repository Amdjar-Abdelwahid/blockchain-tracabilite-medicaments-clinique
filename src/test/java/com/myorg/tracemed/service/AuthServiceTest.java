package com.myorg.tracemed.service;

import com.myorg.tracemed.entity.Organisation;
import com.myorg.tracemed.entity.Role;
import com.myorg.tracemed.entity.Utilisateur;
import com.myorg.tracemed.dto.RegisterRequest;
import com.myorg.tracemed.dto.AuthenticationRequest;
import com.myorg.tracemed.dto.AuthenticationResponse;
import com.myorg.tracemed.repository.UtilisateurRepository;
import com.myorg.tracemed.repository.OrganisationRepository;
import com.myorg.tracemed.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerShouldCreateNewUser() {
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .password("password123")
                .email("newuser@example.com")
                .role(Role.ADMIN)
                .nomComplet("New User")
                .build();

        AuthenticationResponse response = authService.register(request);

        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getRole()).isEqualTo("ADMIN"); // role is String in DTO
        assertThat(utilisateurRepository.findByUsername("newuser")).isPresent();
    }

    @Test
    void authenticateShouldReturnTokenForValidCredentials() {
        // Setup
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("auth-test")
                .password("password123")
                .email("auth@example.com")
                .role(Role.ADMIN)
                .nomComplet("Auth Test")
                .build();
        authService.register(registerRequest);

        // Test
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .username("auth-test")
                .password("password123")
                .build();

        AuthenticationResponse response = authService.authenticate(authRequest);

        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUsername()).isEqualTo("auth-test");
    }

    @Test
    void authenticateShouldFailForInvalidPassword() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("invalid-pass")
                .password("correctpassword")
                .email("invalid@example.com")
                .role(Role.ADMIN)
                .nomComplet("Invalid Test")
                .build();
        authService.register(registerRequest);

        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .username("invalid-pass")
                .password("wrongpassword")
                .build();

        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(Exception.class);
    }
}
