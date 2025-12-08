package com.myorg.tracemed.controller;

import com.myorg.tracemed.entity.Block;
import com.myorg.tracemed.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @PostMapping("/mine")
    public ResponseEntity<String> mineBlock() {
        Block newBlock = blockService.mineBlock();
        if (newBlock == null) {
            return ResponseEntity.ok("Aucune transaction en attente à miner.");
        }
        return ResponseEntity.ok("Nouveau bloc miné ! Hash: " + newBlock.getHash()
                + " | MerkleRoot: " + newBlock.getMerkleRoot()
                + " | Transactions: " + newBlock.getTransactions().size());
    }
}
