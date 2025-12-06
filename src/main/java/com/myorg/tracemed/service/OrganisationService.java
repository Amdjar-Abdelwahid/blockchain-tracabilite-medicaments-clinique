package com.myorg.tracemed.service;

import com.myorg.tracemed.entity.Organisation;
import com.myorg.tracemed.repository.OrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    private final OrganisationRepository repository;

    public List<Organisation> getAllOrganisations() {
        return repository.findAll();
    }

    public Organisation getOrganisationById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation introuvable avec l'id: " + id));
    }

    @Transactional
    public Organisation createOrganisation(Organisation organisation) {
        // Enforce new entity
        organisation.setId(null);
        if (organisation.getDateCreation() == null) {
            organisation.setDateCreation(Instant.now());
        }
        return repository.save(organisation);
    }

    @Transactional
    public Organisation updateOrganisation(Long id, Organisation updatedOrg) {
        Organisation existing = getOrganisationById(id);

        if (updatedOrg.getNom() != null)
            existing.setNom(updatedOrg.getNom());
        if (updatedOrg.getTypeOrganisation() != null)
            existing.setTypeOrganisation(updatedOrg.getTypeOrganisation());
        if (updatedOrg.getIdentifiantExterne() != null)
            existing.setIdentifiantExterne(updatedOrg.getIdentifiantExterne());

        return repository.save(existing);
    }

    @Transactional
    public void deleteOrganisation(Long id) {
        // Check verification: can we delete if users exist? For now, let database
        // constraints handle it (or Cascade).
        // Safest is to just try delete.
        repository.deleteById(id);
    }
}
