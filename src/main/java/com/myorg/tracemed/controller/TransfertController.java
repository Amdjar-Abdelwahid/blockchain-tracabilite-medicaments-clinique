package com.myorg.tracemed.controller;

import com.myorg.tracemed.dto.CreateTransfertRequest;
import com.myorg.tracemed.entity.DemandeTransfert;
import com.myorg.tracemed.service.TransfertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transferts")
public class TransfertController {

    private final TransfertService service;

    public TransfertController(TransfertService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DemandeTransfert> createTransfert(@RequestBody CreateTransfertRequest req) {
        DemandeTransfert d = service.createDemandeTransfert(req);
        return ResponseEntity.ok(d);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<DemandeTransfert> approve(@PathVariable Long id, @RequestParam String username) {
        DemandeTransfert d = service.approveDemande(id, username);
        return ResponseEntity.ok(d);
    }

    @GetMapping
    public ResponseEntity<List<DemandeTransfert>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String demandeur,
            @RequestParam(required = false) Long orgSourceId,
            @RequestParam(required = false) Long orgDestId
    ) {
        return ResponseEntity.ok(
                service.listTransferts(status, demandeur, orgSourceId, orgDestId)
        );
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<DemandeTransfert> cancel(@PathVariable Long id, @RequestParam String username) {
        DemandeTransfert d = service.cancelDemande(id, username);
        return ResponseEntity.ok(d);
    }
}
