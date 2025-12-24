package com.myorg.tracemed.repository;

import com.myorg.tracemed.entity.Medicament;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MedicamentRepositoryTest {

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Test
    void saveMedicamentShouldPersist() {
        Medicament medicament = Medicament.builder()
                .nom("Aspirin")
                .forme("Comprimé")
                .dosage("500mg")
                .build();

        Medicament saved = medicamentRepository.save(medicament);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNom()).isEqualTo("Aspirin");
    }

    @Test
    void findAllShouldReturnAllMedicaments() {
        Medicament med1 = Medicament.builder()
                .nom("Med1")
                .forme("Sirop")
                .build();

        Medicament med2 = Medicament.builder()
                .nom("Med2")
                .forme("Injection")
                .build();

        medicamentRepository.save(med1);
        medicamentRepository.save(med2);

        List<Medicament> result = medicamentRepository.findAll();
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }
}
