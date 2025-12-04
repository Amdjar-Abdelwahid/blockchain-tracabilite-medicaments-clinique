package com.myorg.tracemed.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EvenementDto {
    private Long id;
    private String typeEvenement;
    private String sousType;
    private Long numeroSequence;
    private String detailsJson;
    private String realiseParUsername;
    private Long realiseParId;
    private String realiseParOrganisation;
    private String dateEvenement; // ISO string or fallback to numeroSequence if date absent
}
