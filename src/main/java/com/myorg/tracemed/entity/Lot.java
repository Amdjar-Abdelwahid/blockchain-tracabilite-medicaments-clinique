package com.myorg.tracemed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "lot")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroLot;
    private LocalDate dateFabrication;
    private LocalDate datePeremption;
    private Integer quantite;
    private Instant dateCreation = Instant.now();

    @ManyToOne
    @JoinColumn(name = "medicament_id")
    private Medicament medicament;
}
