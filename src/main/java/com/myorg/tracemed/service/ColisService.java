package com.myorg.tracemed.service;

import com.myorg.tracemed.entity.*;
import com.myorg.tracemed.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ColisService {

    private final ColisPhysiqueRepository colisRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final OrganisationRepository organisationRepo;
    private final EvenementColisRepository eventRepo;

    public ColisService(
            ColisPhysiqueRepository colisRepo,
            UtilisateurRepository utilisateurRepo,
            OrganisationRepository organisationRepo,
            EvenementColisRepository eventRepo
    ) {
        this.colisRepo = colisRepo;
        this.utilisateurRepo = utilisateurRepo;
        this.organisationRepo = organisationRepo;
        this.eventRepo = eventRepo;
    }

    @Transactional
    public ColisPhysique enregistrerReception(String identifiantColis, String username) {

        // 1) retrouver l'utilisateur
        Utilisateur user = utilisateurRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Organisation org = user.getOrganisation();

        // 2) chercher colis existant
        ColisPhysique colis = colisRepo.findByIdentifiantColis(identifiantColis)
                .orElseGet(() -> {
                    // si non existant → création
                    ColisPhysique newColis = ColisPhysique.builder()
                            .identifiantColis(identifiantColis)
                            .dateEmballage(Instant.now())
                            .statut(StatutColis.CREE)
                            .proprietaireActuel(org)
                            .build();
                    return colisRepo.save(newColis);
                });

        // 3) mettre à jour le statut + propriétaire
        colis.setStatut(StatutColis.STOCKE);
        colis.setProprietaireActuel(org);
        colisRepo.save(colis);

        // 4) créer un événement
        Long nextSeq = eventRepo.countByColisPhysique(colis) + 1;

        EvenementColis event = EvenementColis.builder()
                .colisPhysique(colis)
                .realisePar(user)
                .realiseParOrganisation(org)
                .typeEvenement(TypeEvenement.RECU)
                .sousType("RECEPTION_PHARMACIE")
                .numeroSequence(nextSeq)
                .detailsJson("{\"action\":\"reception\"}")
                .dateEnregistrement(Instant.now())
                .build();

        eventRepo.save(event);

        return colis;
    }
}
