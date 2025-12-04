package com.myorg.tracemed.service;

import com.myorg.tracemed.dto.CreateTransfertRequest;
import com.myorg.tracemed.entity.*;
import com.myorg.tracemed.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransfertService {

    private final UtilisateurRepository utilisateurRepo;
    private final OrganisationRepository organisationRepo;
    private final ColisPhysiqueRepository colisRepo;
    private final DemandeTransfertRepository demandeRepo;
    private final EvenementService evenementService;

    public TransfertService(UtilisateurRepository utilisateurRepo,
                            OrganisationRepository organisationRepo,
                            ColisPhysiqueRepository colisRepo,
                            DemandeTransfertRepository demandeRepo,
                            EvenementService evenementService) {
        this.utilisateurRepo = utilisateurRepo;
        this.organisationRepo = organisationRepo;
        this.colisRepo = colisRepo;
        this.demandeRepo = demandeRepo;
        this.evenementService = evenementService;
    }

    @Transactional
    public DemandeTransfert createDemandeTransfert(CreateTransfertRequest req) {
        // validate request
        if (req.getIdentifiantsColis() == null || req.getIdentifiantsColis().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Liste identifiantsColis vide");
        }

        Utilisateur user = utilisateurRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        Organisation orgDest = organisationRepo.findById(req.getOrgDestinationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation destination introuvable"));

        Organisation orgSource = user.getOrganisation();
        if (orgSource == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'utilisateur n'a pas d'organisation source");
        }

        List<ColisPhysique> colisList = new ArrayList<>();
        for (String ident : req.getIdentifiantsColis()) {
            ColisPhysique colis = colisRepo.findByIdentifiantColis(ident)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colis introuvable: " + ident));
            if (colis.getProprietaireActuel() == null ||
                    !colis.getProprietaireActuel().getId().equals(orgSource.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le colis " + ident + " n'appartient pas à l'organisation du demandeur");
            }
            colisList.add(colis);
        }

        DemandeTransfert d = DemandeTransfert.builder()
                .statut("PENDING")
                .dateDemande(Instant.now())
                .orgSource(orgSource)
                .orgDestination(orgDest)
                .demandePar(user)
                .colis(colisList)
                .build();

        return demandeRepo.save(d);
    }

    @Transactional
    public DemandeTransfert approveDemande(Long id, String username) {

        Utilisateur user = utilisateurRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        DemandeTransfert demande = demandeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande non trouvée"));

        // Vérifier que l'utilisateur appartient à l'organisation destination
        if (!user.getOrganisation().getId().equals(demande.getOrgDestination().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Utilisateur non autorisé à approuver cette demande");
        }

        // Vérifier statut
        if (!"PENDING".equals(demande.getStatut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La demande n'est pas en statut PENDING");
        }

        // 1) Mettre à jour le statut
        demande.setStatut("COMPLETED");
        demande.setDateCompletion(Instant.now());

        // 2) Mettre à jour propriétaire des colis + créer événements via EvenementService
        for (ColisPhysique colis : demande.getColis()) {

            // Ancien propriétaire
            Organisation orgSource = colis.getProprietaireActuel();

            // Nouveau propriétaire = organisation destination
            colis.setProprietaireActuel(demande.getOrgDestination());

            // Création événement transfert (numeroSequence/date gérés par EvenementService)
            EvenementColis evenement = EvenementColis.builder()
                    .typeEvenement(TypeEvenement.TRANSFERT)
                    .sousType("VALIDATION_TRANSFERT")
                    .detailsJson("{\"transfert_id\": " + demande.getId() + "}")
                    .colisPhysique(colis)
                    .realisePar(user)
                    .realiseParOrganisation(user.getOrganisation())
                    .build();

            // use EvenementService to compute hash, create tx and save
            evenementService.createEventWithTx(evenement);
        }

        return demandeRepo.save(demande);
    }

    public List<DemandeTransfert> listTransferts(
            String statut,
            String demandeur,
            Long orgSourceId,
            Long orgDestId
    ) {
        if (statut != null) {
            return demandeRepo.findByStatut(statut);
        }
        if (demandeur != null) {
            return demandeRepo.findByDemandePar_Username(demandeur);
        }
        if (orgSourceId != null) {
            return demandeRepo.findByOrgSource_Id(orgSourceId);
        }
        if (orgDestId != null) {
            return demandeRepo.findByOrgDestination_Id(orgDestId);
        }
        return demandeRepo.findAll();
    }

    @Transactional
    public DemandeTransfert cancelDemande(Long id, String username) {
        Utilisateur user = utilisateurRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        DemandeTransfert demande = demandeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande non trouvée"));

        // L'utilisateur doit appartenir à l'organisation SOURCE
        if (demande.getOrgSource() == null ||
                !user.getOrganisation().getId().equals(demande.getOrgSource().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Utilisateur non autorisé à annuler cette demande");
        }

        if (!"PENDING".equals(demande.getStatut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seules les demandes en PENDING peuvent être annulées");
        }

        // Mettre à jour statut
        demande.setStatut("CANCELLED");
        demande.setDateCompletion(Instant.now());

        // Créer événements d'annulation pour chaque colis via EvenementService
        for (ColisPhysique colis : demande.getColis()) {
            EvenementColis ev = EvenementColis.builder()
                    .typeEvenement(TypeEvenement.ANNULATION)
                    .sousType("ANNULATION_DEMANDE")
                    .detailsJson("{\"demande_id\": " + demande.getId() + "}")
                    .colisPhysique(colis)
                    .realisePar(user)
                    .realiseParOrganisation(user.getOrganisation())
                    .build();

            evenementService.createEventWithTx(ev);
        }

        return demandeRepo.save(demande);
    }
}
