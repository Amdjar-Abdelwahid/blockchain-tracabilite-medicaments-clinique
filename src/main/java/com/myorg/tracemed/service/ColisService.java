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
        private final EvenementService evenementService;

        public ColisService(
                        ColisPhysiqueRepository colisRepo,
                        UtilisateurRepository utilisateurRepo,
                        OrganisationRepository organisationRepo,
                        EvenementService evenementService) {
                this.colisRepo = colisRepo;
                this.utilisateurRepo = utilisateurRepo;
                this.organisationRepo = organisationRepo;
                this.evenementService = evenementService;
        }

        @Transactional
        public ColisPhysique enregistrerReception(String identifiantColis, String username) {

                // 1) retrouver l'utilisateur
                Utilisateur user = utilisateurRepo.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

                Organisation org = user.getOrganisation();

                // 2) chercher colis existant (ou créer)
                ColisPhysique colis = colisRepo.findByIdentifiantColis(identifiantColis)
                                .orElseGet(() -> {
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

                // 4) créer un événement - déléguer à EvenementService (hash + tx + sauvegarde)
                EvenementColis event = EvenementColis.builder()
                                .colisPhysique(colis)
                                .realisePar(user)
                                .realiseParOrganisation(org)
                                .typeEvenement(TypeEvenement.RECU)
                                .sousType("RECEPTION_PHARMACIE")
                                .detailsJson("{\"action\":\"reception\"}")
                                // ne PAS définir numeroSequence ni dateEnregistrement : EvenementService gère
                                // ça
                                .build();

                evenementService.createEventWithTx(event);

                return colis;
        }

        @Transactional
        public ColisPhysique administrerMedicament(String identifiantColis, String username) {
                Utilisateur user = utilisateurRepo.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
                Organisation org = user.getOrganisation();

                ColisPhysique colis = colisRepo.findByIdentifiantColis(identifiantColis)
                                .orElseThrow(() -> new RuntimeException("Colis introuvable"));

                colis.setStatut(StatutColis.ADMINISTRE);
                colis.setProprietaireActuel(null); // Consommé, il n'a plus de propriétaire physique
                colisRepo.save(colis);

                EvenementColis event = EvenementColis.builder()
                                .colisPhysique(colis)
                                .realisePar(user)
                                .realiseParOrganisation(org)
                                .typeEvenement(TypeEvenement.CONSOMME)
                                .sousType("ADMINISTRATION_PATIENT")
                                .detailsJson("{\"action\":\"administration\"}")
                                .build();

                evenementService.createEventWithTx(event);
                return colis;
        }

        @Transactional
        public ColisPhysique signalerIncident(String identifiantColis, String username, String details) {
                Utilisateur user = utilisateurRepo.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
                Organisation org = user.getOrganisation();

                ColisPhysique colis = colisRepo.findByIdentifiantColis(identifiantColis)
                                .orElseThrow(() -> new RuntimeException("Colis introuvable"));

                colis.setStatut(StatutColis.SUSPECT);
                colisRepo.save(colis);

                EvenementColis event = EvenementColis.builder()
                                .colisPhysique(colis)
                                .realisePar(user)
                                .realiseParOrganisation(org)
                                .typeEvenement(TypeEvenement.AUTRE)
                                .sousType("INCIDENT")
                                .detailsJson("{\"incident\":\"" + details + "\"}")
                                .build();

                evenementService.createEventWithTx(event);
                return colis;
        }
}
