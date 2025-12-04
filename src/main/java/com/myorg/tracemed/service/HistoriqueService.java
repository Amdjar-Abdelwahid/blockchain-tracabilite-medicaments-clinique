package com.myorg.tracemed.service;

import com.myorg.tracemed.dto.ColisHistoriqueDto;
import com.myorg.tracemed.dto.EvenementDto;
import com.myorg.tracemed.entity.ColisPhysique;
import com.myorg.tracemed.entity.EvenementColis;
import com.myorg.tracemed.repository.ColisPhysiqueRepository;
import com.myorg.tracemed.repository.EvenementColisRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoriqueService {

    private final ColisPhysiqueRepository colisRepo;
    private final EvenementColisRepository eventRepo;

    public HistoriqueService(ColisPhysiqueRepository colisRepo,
            EvenementColisRepository eventRepo) {
        this.colisRepo = colisRepo;
        this.eventRepo = eventRepo;
    }

    public ColisHistoriqueDto getHistorique(String identifiantColis) {
        ColisPhysique colis = colisRepo.findByIdentifiantColis(identifiantColis)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colis introuvable"));

        List<EvenementColis> events = eventRepo.findByColisPhysiqueOrderByNumeroSequenceAsc(colis);

        List<EvenementDto> eventDtos = events.stream().map(ev -> {
            String dateStr = null;
            // tente d'obtenir une date via reflection si la propriété existe
            try {
                java.lang.reflect.Method m = ev.getClass().getMethod("getDateEnregistrement");
                Object val = m.invoke(ev);
                if (val != null)
                    dateStr = val.toString();
            } catch (Exception ignored) {
                // fallback : utiliser numeroSequence comme repère temporel
                dateStr = ev.getNumeroSequence() == null ? null : ev.getNumeroSequence().toString();
            }

            return EvenementDto.builder()
                    .id(ev.getId())
                    .typeEvenement(ev.getTypeEvenement() == null ? null : ev.getTypeEvenement().name())
                    .sousType(ev.getSousType())
                    .numeroSequence(ev.getNumeroSequence())
                    .detailsJson(ev.getDetailsJson())
                    .realiseParUsername(ev.getRealisePar() == null ? null : ev.getRealisePar().getUsername())
                    .realiseParId(ev.getRealisePar() == null ? null : ev.getRealisePar().getId())
                    .realiseParOrganisation(
                            ev.getRealiseParOrganisation() == null ? null : ev.getRealiseParOrganisation().getNom())
                    .dateEvenement(dateStr)
                    .hashTx(ev.getTxBlockchain() == null ? null : ev.getTxBlockchain().getHashTx())
                    .previousHash(ev.getTxBlockchain() == null ? null : ev.getTxBlockchain().getPreviousHash())
                    .build();
        }).collect(Collectors.toList());

        // <-- ici on convertit l'enum statut en String avec .name()
        String statutStr = colis.getStatut() == null ? null : colis.getStatut().name();

        return ColisHistoriqueDto.builder()
                .colisId(colis.getId())
                .identifiantColis(colis.getIdentifiantColis())
                .statut(statutStr)
                .proprietaireActuelId(
                        colis.getProprietaireActuel() == null ? null : colis.getProprietaireActuel().getId())
                .proprietaireActuelNom(
                        colis.getProprietaireActuel() == null ? null : colis.getProprietaireActuel().getNom())
                .evenements(eventDtos)
                .build();
    }
}
