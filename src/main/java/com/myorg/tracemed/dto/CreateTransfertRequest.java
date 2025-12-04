package com.myorg.tracemed.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateTransfertRequest {
    private String username;
    private Long orgDestinationId;
    private List<String> identifiantsColis;
}
