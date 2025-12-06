package com.myorg.tracemed.service;

import com.myorg.tracemed.entity.Medicament;
import com.myorg.tracemed.repository.MedicamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentService {

    private final MedicamentRepository repository;

    public List<Medicament> getAllMedicaments() {
        return repository.findAll();
    }

    public Medicament getMedicamentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médicament introuvable avec l'id: " + id));
    }

    @Transactional
    public Medicament createMedicament(Medicament medicament) {
        medicament.setId(null);
        if (medicament.getDateCreation() == null) {
            medicament.setDateCreation(Instant.now());
        }
        return repository.save(medicament);
    }

    @Transactional
    public Medicament updateMedicament(Long id, Medicament updatedMed) {
        Medicament existing = getMedicamentById(id);

        if (updatedMed.getNom() != null)
            existing.setNom(updatedMed.getNom());
        if (updatedMed.getCodeGTIN() != null)
            existing.setCodeGTIN(updatedMed.getCodeGTIN());
        if (updatedMed.getForme() != null)
            existing.setForme(updatedMed.getForme());
        if (updatedMed.getDosage() != null)
            existing.setDosage(updatedMed.getDosage());
        if (updatedMed.getConditionnement() != null)
            existing.setConditionnement(updatedMed.getConditionnement());

        return repository.save(existing);
    }

    @Transactional
    public void deleteMedicament(Long id) {
        repository.deleteById(id);
    }
}
