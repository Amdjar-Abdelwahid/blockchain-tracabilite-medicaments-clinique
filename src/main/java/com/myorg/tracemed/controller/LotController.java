package com.myorg.tracemed.controller;

import com.myorg.tracemed.dto.LotDto;
import com.myorg.tracemed.entity.Lot;
import com.myorg.tracemed.service.LotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
public class LotController {

    private final LotService service;

    @GetMapping
    public ResponseEntity<List<Lot>> getAll() {
        return ResponseEntity.ok(service.getAllLots());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lot> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getLotById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN') or hasRole('LABORATOIRE')")
    public ResponseEntity<Lot> create(@RequestBody LotDto request) {
        return ResponseEntity.ok(service.createLot(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN') or hasRole('LABORATOIRE')")
    public ResponseEntity<Lot> update(@PathVariable Long id, @RequestBody LotDto request) {
        return ResponseEntity.ok(service.updateLot(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteLot(id);
        return ResponseEntity.noContent().build();
    }
}
