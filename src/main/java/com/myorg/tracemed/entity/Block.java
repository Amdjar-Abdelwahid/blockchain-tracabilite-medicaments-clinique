package com.myorg.tracemed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "block")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The hash of this block header
    private String hash;

    // Link to previous GLOBAL block
    private String previousBlockHash;

    // The Merkle Root of all transactions in this block
    private String merkleRoot;

    private Instant timestamp = Instant.now();

    private Long nonce; // For Proof of Work (optional, but good practice field)

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TransactionBlockchain> transactions = new ArrayList<>();
}
