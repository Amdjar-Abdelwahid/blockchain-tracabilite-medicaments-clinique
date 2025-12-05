package com.myorg.tracemed.controller;

import com.myorg.tracemed.dto.AuditResultDto;
import com.myorg.tracemed.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit", description = "Vérification d'intégrité de la blockchain")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/{identifiantColis}")
    @Operation(summary = "Vérifier l'intégrité d'un colis", description = "Recalcule tous les hashs de la chaîne pour vérifier si les données ont été altérées.")
    public ResponseEntity<AuditResultDto> auditColis(@PathVariable String identifiantColis) {
        return ResponseEntity.ok(auditService.auditColis(identifiantColis));
    }
}
