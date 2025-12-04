package com.myorg.tracemed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "transaction_blockchain")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionBlockchain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hashTx;
    private String idReseau;
    private Long numeroBloc;
    private Instant dateBloc;
    private String urlExplorateur;
    private String previousHash;
    private Instant dateCreation = Instant.now();
}
