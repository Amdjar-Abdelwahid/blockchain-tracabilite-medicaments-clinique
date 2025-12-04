package com.myorg.tracemed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "evenement_colis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EvenementColis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeEvenement typeEvenement; // RECU, TRANSFERT, ADMINISTRE, AUTRE

    private String sousType;

    @Lob
    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    private Instant dateEnregistrement = Instant.now();

    private Long numeroSequence; // set atomically in service

    @Column(name = "hash_details", length = 128)
    private String hashDetails;

    @ManyToOne
    @JoinColumn(name = "colis_physique_id")
    private ColisPhysique colisPhysique;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur realisePar;

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation realiseParOrganisation;

    @OneToOne
    @JoinColumn(name = "tx_blockchain_id")
    private TransactionBlockchain txBlockchain;
}
