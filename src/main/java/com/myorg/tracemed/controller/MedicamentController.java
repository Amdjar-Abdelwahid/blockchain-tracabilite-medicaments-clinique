package com.myorg.tracemed.controller;

import com.myorg.tracemed.entity.Medicament;
import com.myorg.tracemed.service.MedicamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicaments")
@RequiredArgsConstructor
public class MedicamentController {

    private final MedicamentService service;

    @GetMapping
    public ResponseEntity<List<Medicament>> getAll() {
        return ResponseEntity.ok(service.getAllMedicaments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicament> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMedicamentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LABORATOIRE')")
    public ResponseEntity<Medicament> create(@RequestBody Medicament medicament) {
        return ResponseEntity.ok(service.createMedicament(medicament));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LABORATOIRE')")
    public ResponseEntity<Medicament> update(@PathVariable Long id, @RequestBody Medicament medicament) {
        return ResponseEntity.ok(service.updateMedicament(id, medicament));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteMedicament(id);
        return ResponseEntity.noContent().build();
    }
}
