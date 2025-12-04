package com.myorg.tracemed.controller;

import com.myorg.tracemed.dto.ColisHistoriqueDto;
import com.myorg.tracemed.service.HistoriqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colis")
public class HistoriqueController {

    private final HistoriqueService historiqueService;

    public HistoriqueController(HistoriqueService historiqueService) {
        this.historiqueService = historiqueService;
    }

    @GetMapping("/{identifiant}/historique")
    public ResponseEntity<ColisHistoriqueDto> getHistorique(@PathVariable String identifiant) {
        return ResponseEntity.ok(historiqueService.getHistorique(identifiant));
    }
}
