package com.myorg.tracemed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotDto {
    private String numeroLot;
    private LocalDate dateFabrication;
    private LocalDate datePeremption;
    private Integer quantite;
    private Long medicamentId;
}
