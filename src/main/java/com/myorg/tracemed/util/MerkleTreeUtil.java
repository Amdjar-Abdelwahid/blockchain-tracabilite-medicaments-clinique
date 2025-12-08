package com.myorg.tracemed.util;

import java.util.ArrayList;
import java.util.List;

public class MerkleTreeUtil {

    private MerkleTreeUtil() {
    }

    /**
     * Calculates the Merkle Root for a list of transaction hashes.
     */
    public static String calculateMerkleRoot(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return ""; // Or throw exception, but empty root for empty block is fine
        }

        // Recursively reduce the list
        List<String> previousTreeLayer = new ArrayList<>(hashes);
        List<String> treeLayer = previousTreeLayer;

        while (treeLayer.size() > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 0; i < previousTreeLayer.size(); i += 2) {
                String left = previousTreeLayer.get(i);
                String right = (i + 1 < previousTreeLayer.size())
                        ? previousTreeLayer.get(i + 1)
                        : left; // Duplicate last if odd number

                // Hash (Left + Right)
                treeLayer.add(HashUtil.sha256Hex(left + right));
            }
            previousTreeLayer = treeLayer;
        }

        return treeLayer.get(0);
    }
}
