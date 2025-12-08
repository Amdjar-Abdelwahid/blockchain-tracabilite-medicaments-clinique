package com.myorg.tracemed.controller;

import com.myorg.tracemed.entity.ColisPhysique;
import com.myorg.tracemed.service.ColisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colis")
public class ColisController {

    private final ColisService colisService;
    private final com.myorg.tracemed.service.QRCodeService qrCodeService;

    public ColisController(ColisService colisService, com.myorg.tracemed.service.QRCodeService qrCodeService) {
        this.colisService = colisService;
        this.qrCodeService = qrCodeService;
    }

    @PostMapping("/reception")
    public ColisPhysique reception(@RequestParam String identifiant,
            @RequestParam String username) {
        return colisService.enregistrerReception(identifiant, username);
    }

    @GetMapping(value = "/{identifiant}/qrcode", produces = org.springframework.http.MediaType.IMAGE_PNG_VALUE)
    public byte[] getQRCode(@PathVariable String identifiant) {
        // Point to a theoretical public URL or the audit API
        String text = "http://localhost:8080/api/audit/" + identifiant;
        return qrCodeService.generateQRCodeImage(text, 300, 300);
    }
}
