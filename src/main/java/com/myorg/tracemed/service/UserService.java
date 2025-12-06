package com.myorg.tracemed.service;

import com.myorg.tracemed.dto.UpdateUserRequest;
import com.myorg.tracemed.entity.Organisation;
import com.myorg.tracemed.entity.Utilisateur;
import com.myorg.tracemed.repository.OrganisationRepository;
import com.myorg.tracemed.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UtilisateurRepository utilisateurRepository;
    private final OrganisationRepository organisationRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Utilisateur> getAllUsers() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur getUserById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'id: " + id));
    }

    @Transactional
    public Utilisateur updateUser(Long id, UpdateUserRequest request) {
        Utilisateur user = getUserById(id);

        if (request.getNomComplet() != null) {
            user.setNomComplet(request.getNomComplet());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getOrganisationId() != null) {
            Organisation org = organisationRepository.findById(request.getOrganisationId())
                    .orElseThrow(() -> new RuntimeException("Organisation introuvable"));
            user.setOrganisation(org);
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return utilisateurRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        Utilisateur user = getUserById(id);
        utilisateurRepository.delete(user);
    }
}
