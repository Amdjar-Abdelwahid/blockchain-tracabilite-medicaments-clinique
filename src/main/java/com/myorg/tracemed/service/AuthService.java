package com.myorg.tracemed.service;

import com.myorg.tracemed.dto.AuthenticationRequest;
import com.myorg.tracemed.dto.AuthenticationResponse;
import com.myorg.tracemed.dto.RegisterRequest;
import com.myorg.tracemed.entity.Organisation;
import com.myorg.tracemed.entity.Utilisateur;
import com.myorg.tracemed.repository.OrganisationRepository;
import com.myorg.tracemed.repository.UtilisateurRepository;
import com.myorg.tracemed.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UtilisateurRepository repository;
        private final OrganisationRepository organisationRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;

        public AuthenticationResponse register(RegisterRequest request) {
                Organisation organisation = null;
                if (request.getOrganisationId() != null) {
                        organisation = organisationRepository.findById(request.getOrganisationId()).orElse(null);
                }

                var user = Utilisateur.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .nomComplet(request.getNomComplet())
                                .email(request.getEmail())
                                .role(request.getRole())
                                .dateCreation(java.time.Instant.now())
                                .organisation(organisation);

                // Generate Key Pair
                java.util.Map<String, String> keys = com.myorg.tracemed.util.SignatureUtil.generateKeyPair();
                user.publicKey(keys.get("publicKey"));
                user.privateKey(keys.get("privateKey"));

                Utilisateur savedUser = user.build();

                repository.save(savedUser);

                // Return token or just success. Let's return token.
                // We need to construct UserDetails from Utilisateur manually since our Entity
                // doesn't implement it
                // Or fetch it via the UserDetailsService (but we just saved it).
                // Let's create a UserDetails adapter.
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(savedUser.getUsername())
                                .password(savedUser.getPassword())
                                .roles(savedUser.getRole() != null ? savedUser.getRole().name() : "USER")
                                .build();

                var jwtToken = jwtUtils.generateToken(userDetails);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .role(savedUser.getRole() != null ? savedUser.getRole().name() : "USER")
                                .username(savedUser.getUsername())
                                .organisationId(savedUser.getOrganisation() != null
                                                ? savedUser.getOrganisation().getId()
                                                : null)
                                .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));
                var user = repository.findByUsername(request.getUsername())
                                .orElseThrow();

                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .roles(user.getRole() != null ? user.getRole().name() : "USER")
                                .build();

                var jwtToken = jwtUtils.generateToken(userDetails);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .role(user.getRole() != null ? user.getRole().name() : "USER")
                                .username(user.getUsername())
                                .organisationId(user.getOrganisation() != null ? user.getOrganisation().getId() : null)
                                .build();
        }
}
