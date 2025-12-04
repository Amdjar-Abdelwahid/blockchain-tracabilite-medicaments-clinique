package com.myorg.tracemed.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ColisHistoriqueDto {
    private Long colisId;
    private String identifiantColis;
    private String statut;
    private Long proprietaireActuelId;
    private String proprietaireActuelNom;
    private List<EvenementDto> evenements;
}
