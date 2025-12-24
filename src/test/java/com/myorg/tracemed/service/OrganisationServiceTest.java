package com.myorg.tracemed.service;

import com.myorg.tracemed.entity.Organisation;
import com.myorg.tracemed.repository.OrganisationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrganisationServiceTest {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Test
    void createOrganisationShouldPersist() {
        Organisation org = Organisation.builder()
                .nom("Test Clinic")
                .typeOrganisation("CLINIQUE")
                .dateCreation(Instant.now())
                .build();

        Organisation saved = organisationService.createOrganisation(org);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNom()).isEqualTo("Test Clinic");
        assertThat(organisationRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void getAllOrganisationsShouldReturnList() {
        Organisation org1 = Organisation.builder()
                .nom("Org 1")
                .typeOrganisation("CLINIQUE")
                .dateCreation(Instant.now())
                .build();
        Organisation org2 = Organisation.builder()
                .nom("Org 2")
                .typeOrganisation("PHARMACIE")
                .dateCreation(Instant.now())
                .build();

        organisationRepository.save(org1);
        organisationRepository.save(org2);

        List<Organisation> result = organisationService.getAllOrganisations();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void getOrganisationByIdShouldReturnCorrectEntity() {
        Organisation org = Organisation.builder()
                .nom("Find Me")
                .typeOrganisation("CLINIQUE")
                .dateCreation(Instant.now())
                .build();
        Organisation saved = organisationRepository.save(org);

        Organisation result = organisationService.getOrganisationById(saved.getId());

        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("Find Me");
    }
}
