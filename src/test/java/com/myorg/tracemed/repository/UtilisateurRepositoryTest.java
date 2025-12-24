package com.myorg.tracemed.repository;

import com.myorg.tracemed.entity.Role;
import com.myorg.tracemed.entity.Utilisateur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UtilisateurRepositoryTest {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Test
    void findByUsernameShouldReturnSavedUser() {
        Utilisateur user = Utilisateur.builder()
                .username("repo-user")
                .password("pwd")
                .role(Role.ADMIN)
                .nomComplet("Repo User")
                .email("repo@example.com")
                .dateCreation(Instant.now())
                .build();

        utilisateurRepository.save(user);

        assertThat(utilisateurRepository.findByUsername("repo-user"))
                .isPresent()
                .get()
                .extracting(Utilisateur::getEmail)
                .isEqualTo("repo@example.com");
    }
}
