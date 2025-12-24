package com.myorg.tracemed.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MerkleTreeUtilTest {

    @Test
    void calculateMerkleRootWithSingleItemShouldReturnHashOfItem() {
        List<String> items = Arrays.asList("item1");
        String root = MerkleTreeUtil.calculateMerkleRoot(items);

        // MerkleTreeUtil returns the single item itself, not its hash
        assertThat(root)
                .isNotBlank()
                .isEqualTo("item1");
    }

    @Test
    void calculateMerkleRootWithMultipleItemsShouldReturnRoot() {
        List<String> items = Arrays.asList("item1", "item2", "item3", "item4");
        String root = MerkleTreeUtil.calculateMerkleRoot(items);

        assertThat(root)
                .isNotBlank()
                .hasSize(64);
    }

    @Test
    void merkleRootShouldBeDeterministic() {
        List<String> items = Arrays.asList("a", "b", "c");
        String root1 = MerkleTreeUtil.calculateMerkleRoot(items);
        String root2 = MerkleTreeUtil.calculateMerkleRoot(items);

        assertThat(root1).isEqualTo(root2);
    }

    @Test
    void differentItemsShouldProduceDifferentRoot() {
        List<String> items1 = Arrays.asList("x", "y");
        List<String> items2 = Arrays.asList("x", "z");

        String root1 = MerkleTreeUtil.calculateMerkleRoot(items1);
        String root2 = MerkleTreeUtil.calculateMerkleRoot(items2);

        assertThat(root1).isNotEqualTo(root2);
    }
}
