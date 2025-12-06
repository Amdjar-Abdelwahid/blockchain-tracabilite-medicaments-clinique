package com.myorg.tracemed.service;

import com.myorg.tracemed.dto.LotDto;
import com.myorg.tracemed.entity.Lot;
import com.myorg.tracemed.entity.Medicament;
import com.myorg.tracemed.repository.LotRepository;
import com.myorg.tracemed.repository.MedicamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LotService {

    private final LotRepository lotRepository;
    private final MedicamentRepository medicamentRepository;

    public List<Lot> getAllLots() {
        return lotRepository.findAll();
    }

    public Lot getLotById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lot introuvable avec l'id: " + id));
    }

    @Transactional
    public Lot createLot(LotDto request) {
        Medicament medicament = medicamentRepository.findById(request.getMedicamentId())
                .orElseThrow(() -> new RuntimeException("Médicament introuvable"));

        Lot lot = Lot.builder()
                .numeroLot(request.getNumeroLot())
                .dateFabrication(request.getDateFabrication())
                .datePeremption(request.getDatePeremption())
                .quantite(request.getQuantite())
                .medicament(medicament)
                .dateCreation(Instant.now())
                .build();

        return lotRepository.save(lot);
    }

    @Transactional
    public Lot updateLot(Long id, LotDto request) {
        Lot existing = getLotById(id);

        if (request.getNumeroLot() != null)
            existing.setNumeroLot(request.getNumeroLot());
        if (request.getDateFabrication() != null)
            existing.setDateFabrication(request.getDateFabrication());
        if (request.getDatePeremption() != null)
            existing.setDatePeremption(request.getDatePeremption());
        if (request.getQuantite() != null)
            existing.setQuantite(request.getQuantite());

        if (request.getMedicamentId() != null) {
            Medicament medicament = medicamentRepository.findById(request.getMedicamentId())
                    .orElseThrow(() -> new RuntimeException("Médicament introuvable"));
            existing.setMedicament(medicament);
        }

        return lotRepository.save(existing);
    }

    @Transactional
    public void deleteLot(Long id) {
        lotRepository.deleteById(id);
    }
}
