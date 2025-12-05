package com.myorg.tracemed.dto;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditResultDto {
    private String status; // VALID, CORRUPTED
    private String identifiantColis;
    private Instant auditTime;
    private String details;
}
