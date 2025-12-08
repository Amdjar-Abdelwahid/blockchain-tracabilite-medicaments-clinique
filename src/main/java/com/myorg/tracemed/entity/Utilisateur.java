package com.myorg.tracemed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String nomComplet;

    private String email;

    private String password;

    @Column(columnDefinition = "TEXT")
    private String publicKey;

    @Column(columnDefinition = "TEXT")
    private String privateKey;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Instant dateCreation = Instant.now();

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;
}
