package com.myorg.tracemed.controller;

import com.myorg.tracemed.config.DataSeeder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final DataSeeder dataSeeder;

    @PostMapping("/seed")
    public ResponseEntity<String> forceSeedData() {
        try {
            dataSeeder.run(); // Re-run the seeding logic
            return ResponseEntity.ok("Données injectées avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur: " + e.getMessage());
        }
    }
}
