package com.myorg.tracemed.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashUtilTest {

    @Test
    void sha256HexShouldProduceSHA256() {
        String result = HashUtil.sha256Hex("hello");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(64); // SHA-256 = 64 hex characters
    }

    @Test
    void sameInputShouldProduceSameHash() {
        String hash1 = HashUtil.sha256Hex("test");
        String hash2 = HashUtil.sha256Hex("test");

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void differentInputsShouldProduceDifferentHashes() {
        String hash1 = HashUtil.sha256Hex("input1");
        String hash2 = HashUtil.sha256Hex("input2");

        assertThat(hash1).isNotEqualTo(hash2);
    }
}
