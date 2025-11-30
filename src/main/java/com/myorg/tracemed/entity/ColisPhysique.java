package com.myorg.tracemed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "colis_physique")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ColisPhysique {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifiantColis; // QR / RFID / serial

    private Instant dateEmballage;

    @Enumerated(EnumType.STRING)
    private StatutColis statut;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @ManyToOne
    @JoinColumn(name = "proprietaire_actuel_id")
    private Organisation proprietaireActuel;

    @OneToMany(mappedBy = "colisPhysique", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroSequence ASC")
    private List<EvenementColis> evenements = new ArrayList<>();
}
