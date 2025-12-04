package com.myorg.tracemed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "demande_transfert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeTransfert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PENDING, APPROVED, REJECTED, COMPLETED
    private String statut;

    private Instant dateDemande;
    private Instant dateCompletion;

    // Organisation qui envoie
    @ManyToOne
    @JoinColumn(name = "org_source_id")
    private Organisation orgSource;

    // Organisation qui reçoit
    @ManyToOne
    @JoinColumn(name = "org_destination_id")
    private Organisation orgDestination;

    // Utilisateur qui a créé la demande
    @ManyToOne
    @JoinColumn(name = "demande_par_id")
    private Utilisateur demandePar;

    // Colis concernés par la demande
    @ManyToMany
    @JoinTable(
            name = "demande_transfert_colis",
            joinColumns = @JoinColumn(name = "demande_transfert_id"),
            inverseJoinColumns = @JoinColumn(name = "colis_id")
    )
    private List<ColisPhysique> colis;
}
