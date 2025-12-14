package com.myorg.tracemed.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.tracemed.entity.Block;
import com.myorg.tracemed.service.FileLedgerService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class BlockchainTest {

    public static void main(String[] args) {
        System.out.println("--- Starting Blockchain Logic Test ---");

        testMerkleTree();
        testFileLedger();

        System.out.println("--- Test Complete ---");
    }

    private static void testMerkleTree() {
        System.out.println("\n1. Testing Merkle Tree...");
        List<String> transactions = Arrays.asList(
                HashUtil.sha256Hex("tx1"),
                HashUtil.sha256Hex("tx2"),
                HashUtil.sha256Hex("tx3"),
                HashUtil.sha256Hex("tx4"));

        String root = MerkleTreeUtil.calculateMerkleRoot(transactions);
        System.out.println("Merkle Root: " + root);

        if (root != null && root.length() == 64) {
            System.out.println("[OK] Merkle Root generated.");
        } else {
            System.err.println("[FAIL] Invalid Merkle Root.");
        }

        // Test consistence
        String root2 = MerkleTreeUtil.calculateMerkleRoot(transactions);
        if (root.equals(root2)) {
            System.out.println("[OK] Merkle Root deterministic.");
        } else {
            System.err.println("[FAIL] Merkle Root non-deterministic.");
        }
    }

    private static void testFileLedger() {
        System.out.println("\n2. Testing File Ledger...");
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

            FileLedgerService ledger = new FileLedgerService(mapper);

            Block b = new Block();
            b.setId(999L);
            b.setHash("test_hash_123");
            b.setMerkleRoot("root_123");

            ledger.writeBlock(b);

            File f = new File("blockchain_data/block_999.json");
            if (f.exists()) {
                System.out.println("[OK] Ledger file created: " + f.getAbsolutePath());
                // cleanup
                f.delete();
            } else {
                System.err.println("[FAIL] Ledger file NOT created.");
            }

        } catch (Exception e) {
            System.err.println("[FAIL] Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
