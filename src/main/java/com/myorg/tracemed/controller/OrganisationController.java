package com.myorg.tracemed.controller;

import com.myorg.tracemed.entity.Organisation;
import com.myorg.tracemed.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organisations")
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService service;

    @GetMapping
    public ResponseEntity<List<Organisation>> getAll() {
        return ResponseEntity.ok(service.getAllOrganisations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organisation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOrganisationById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Organisation> create(@RequestBody Organisation organisation) {
        return ResponseEntity.ok(service.createOrganisation(organisation));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Organisation> update(@PathVariable Long id, @RequestBody Organisation organisation) {
        return ResponseEntity.ok(service.updateOrganisation(id, organisation));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteOrganisation(id);
        return ResponseEntity.noContent().build();
    }
}
