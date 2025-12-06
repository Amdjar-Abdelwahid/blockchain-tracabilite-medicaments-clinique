package com.myorg.tracemed.config;

import com.myorg.tracemed.entity.Role;
import com.myorg.tracemed.entity.Utilisateur;
import com.myorg.tracemed.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

        private final UtilisateurRepository utilisateurRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                if (utilisateurRepository.findByUsername("admin").isEmpty()) {
                        Utilisateur admin = Utilisateur.builder()
                                        .username("admin")
                                        .password(passwordEncoder.encode("admin123"))
                                        .role(Role.ADMIN)
                                        .nomComplet("Administrateur Système")
                                        .email("admin@tracemed.com")
                                        .dateCreation(Instant.now())
                                        .build();
                        utilisateurRepository.save(admin);
                        System.out.println("ADMIN user created: username=admin, password=admin123");
                }
        }
}
