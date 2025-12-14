package com.myorg.tracemed.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.tracemed.entity.Block;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileLedgerService {

    private final String DATA_DIR = "blockchain_data";
    private final ObjectMapper objectMapper;

    public FileLedgerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        createDataDir();
    }

    private void createDataDir() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create blockchain data directory", e);
        }
    }

    public void writeBlock(Block block) {
        try {
            String filename = "block_" + block.getId() + ".json";
            Path path = Paths.get(DATA_DIR, filename);
            // Pretty print for readability
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), block);
            System.out.println("💾 Ledger: Block " + block.getId() + " written to " + path.toAbsolutePath());
        } catch (IOException e) {
            System.err
                    .println("❌ Ledger Error: Could not write block " + block.getId() + " to disk: " + e.getMessage());
        }
    }
}
