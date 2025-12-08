package com.myorg.tracemed.service;

import com.myorg.tracemed.entity.EvenementColis;
import com.myorg.tracemed.entity.TransactionBlockchain;
import com.myorg.tracemed.repository.EvenementColisRepository;
import com.myorg.tracemed.repository.TransactionBlockchainRepository;
import com.myorg.tracemed.util.HashUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class EvenementService {

    private final TransactionBlockchainRepository txRepo;
    private final EvenementColisRepository eventRepo;

    public EvenementService(TransactionBlockchainRepository txRepo,
            EvenementColisRepository eventRepo) {
        this.txRepo = txRepo;
        this.eventRepo = eventRepo;
    }

    @Transactional
    public EvenementColis createEventWithTx(EvenementColis evenement) {
        // 1) date / seq
        if (evenement.getDateEnregistrement() == null) {
            evenement.setDateEnregistrement(Instant.now());
        }

        // Build a stable sequence number per colis: count + 1 (simple)
        Long count = 0L;
        if (evenement.getColisPhysique() != null) {
            count = eventRepo.countByColisPhysique(evenement.getColisPhysique());
        }
        evenement.setNumeroSequence((count == null ? 0L : count) + 1L);

        // 2) compute previous hash
        String previousHash = "0000000000000000000000000000000000000000000000000000000000000000"; // Genesis
        if (evenement.getNumeroSequence() > 1 && evenement.getColisPhysique() != null) {
            Optional<EvenementColis> prevEvent = eventRepo.findByColisPhysiqueAndNumeroSequence(
                    evenement.getColisPhysique(), evenement.getNumeroSequence() - 1);
            if (prevEvent.isPresent() && prevEvent.get().getTxBlockchain() != null) {
                previousHash = prevEvent.get().getTxBlockchain().getHashTx();
            }
        }

        // 3) compute payload to hash (concatenate relevant fields + previousHash)
        String payload = (evenement.getDetailsJson() == null ? "" : evenement.getDetailsJson())
                + "|" + (evenement.getTypeEvenement() == null ? "" : evenement.getTypeEvenement().name())
                + "|" + (evenement.getSousType() == null ? "" : evenement.getSousType())
                + "|"
                + (evenement.getColisPhysique() == null ? "" : String.valueOf(evenement.getColisPhysique().getId()))
                + "|" + evenement.getNumeroSequence()
                + "|" + previousHash;

        String hash = HashUtil.sha256Hex(payload);
        evenement.setHashDetails(hash);

        // 4) create or reuse TransactionBlockchain with same hash
        String finalPreviousHash = previousHash;
        Optional<TransactionBlockchain> existing = txRepo.findByHashTx(hash);
        TransactionBlockchain tx = existing.orElseGet(() -> {

            String signature = null;
            String signerId = null;

            // Sign logic
            if (evenement.getRealisePar() != null && evenement.getRealisePar().getPrivateKey() != null) {
                try {
                    signature = com.myorg.tracemed.util.SignatureUtil.sign(hash,
                            evenement.getRealisePar().getPrivateKey());
                    signerId = evenement.getRealisePar().getUsername();
                } catch (Exception e) {
                    throw new RuntimeException("Erreur lors de la signature de la transaction", e);
                }
            }

            TransactionBlockchain t = TransactionBlockchain.builder()
                    .hashTx(hash)
                    .previousHash(finalPreviousHash)
                    .idReseau("local")
                    .dateCreation(Instant.now())
                    .signature(signature)
                    .signatairePublicId(signerId)
                    .build();
            return txRepo.save(t);
        });

        // 5) link and save event
        evenement.setTxBlockchain(tx);
        EvenementColis saved = eventRepo.save(evenement);

        return saved;
    }
}
