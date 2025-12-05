package com.myorg.tracemed.service;

import com.myorg.tracemed.dto.AuditResultDto;
import com.myorg.tracemed.entity.ColisPhysique;
import com.myorg.tracemed.entity.EvenementColis;
import com.myorg.tracemed.entity.TransactionBlockchain;
import com.myorg.tracemed.repository.ColisPhysiqueRepository;
import com.myorg.tracemed.repository.EvenementColisRepository;
import com.myorg.tracemed.util.HashUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class AuditService {

    private final ColisPhysiqueRepository colisRepo;
    private final EvenementColisRepository eventRepo;

    public AuditService(ColisPhysiqueRepository colisRepo, EvenementColisRepository eventRepo) {
        this.colisRepo = colisRepo;
        this.eventRepo = eventRepo;
    }

    public AuditResultDto auditColis(String identifiantColis) {
        ColisPhysique colis = colisRepo.findByIdentifiantColis(identifiantColis)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colis introuvable"));

        List<EvenementColis> events = eventRepo.findByColisPhysiqueOrderByNumeroSequenceAsc(colis);

        if (events.isEmpty()) {
            return AuditResultDto.builder()
                    .status("VALID")
                    .identifiantColis(identifiantColis)
                    .auditTime(Instant.now())
                    .details("Aucun événement à auditer")
                    .build();
        }

        String computedPreviousHash = "0000000000000000000000000000000000000000000000000000000000000000";

        for (EvenementColis event : events) {
            TransactionBlockchain tx = event.getTxBlockchain();
            if (tx == null) {
                return errorResult(identifiantColis,
                        "Transaction manquante pour l'événement SEQ=" + event.getNumeroSequence());
            }

            // 1. Verify previous hash link
            String storedPreviousHash = tx.getPreviousHash();
            if (event.getNumeroSequence() == 1) {
                if (storedPreviousHash != null && !storedPreviousHash
                        .equals("0000000000000000000000000000000000000000000000000000000000000000")) {
                    // For genesis, we might want to be lenient if null, but stricter if it's set to
                    // something else.
                    // Based on EvenementService, we set it to the genesis string.
                    if (!storedPreviousHash.equals(computedPreviousHash)) {
                        return errorResult(identifiantColis, "Hash précédent incorrect pour GENESIS (SEQ=1)");
                    }
                }
            } else {
                if (!computedPreviousHash.equals(storedPreviousHash)) {
                    return errorResult(identifiantColis, "Chaînage brisé à SEQ=" + event.getNumeroSequence()
                            + ". Attendu: " + computedPreviousHash
                            + ", Trouvé: " + storedPreviousHash);
                }
            }

            // 2. Re-calculate hash of current event
            String payload = (event.getDetailsJson() == null ? "" : event.getDetailsJson())
                    + "|" + (event.getTypeEvenement() == null ? "" : event.getTypeEvenement().name())
                    + "|" + (event.getSousType() == null ? "" : event.getSousType())
                    + "|" + (event.getColisPhysique() == null ? "" : String.valueOf(event.getColisPhysique().getId()))
                    + "|" + event.getNumeroSequence()
                    + "|" + computedPreviousHash;

            String recomputedHash = HashUtil.sha256Hex(payload);
            String storedHash = tx.getHashTx();

            if (!recomputedHash.equals(storedHash)) {
                return errorResult(identifiantColis, "Intégrité compromise à SEQ=" + event.getNumeroSequence()
                        + ". Données modifiées. Hash recalculé=" + recomputedHash + ", Hash stocké=" + storedHash);
            }

            // Advance
            computedPreviousHash = storedHash;
        }

        return AuditResultDto.builder()
                .status("VALID")
                .identifiantColis(identifiantColis)
                .auditTime(Instant.now())
                .details("Intégrité vérifiée sur " + events.size() + " blocs.")
                .build();
    }

    private AuditResultDto errorResult(String id, String msg) {
        return AuditResultDto.builder()
                .status("CORRUPTED")
                .identifiantColis(id)
                .auditTime(Instant.now())
                .details(msg)
                .build();
    }
}
