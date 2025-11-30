package com.myorg.tracemed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "medicament")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Medicament {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codeGTIN;
    private String nom;
    private String forme;
    private String dosage;
    private String conditionnement;

    private Instant dateCreation = Instant.now();
}
