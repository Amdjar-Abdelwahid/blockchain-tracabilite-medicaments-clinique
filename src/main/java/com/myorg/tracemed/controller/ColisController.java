package com.myorg.tracemed.controller;

import com.myorg.tracemed.entity.ColisPhysique;
import com.myorg.tracemed.service.ColisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colis")
public class ColisController {

    private final ColisService colisService;

    public ColisController(ColisService colisService) {
        this.colisService = colisService;
    }

    @PostMapping("/reception")
    public ColisPhysique reception(@RequestParam String identifiant,
                                   @RequestParam String username) {
        return colisService.enregistrerReception(identifiant, username);
    }
}
