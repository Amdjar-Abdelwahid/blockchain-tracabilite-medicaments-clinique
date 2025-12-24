package com.myorg.tracemed.repository;

import com.myorg.tracemed.entity.Block;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BlockRepositoryTest {

    @Autowired
    private BlockRepository blockRepository;

    @Test
    void findTopBlockShouldReturnLastSaved() {
        Block block = Block.builder()
                .hash("block-hash-1")
                .previousBlockHash("0x000")
                .merkleRoot("merkle-root")
                .build();

        blockRepository.save(block);

        assertThat(blockRepository.findTopByOrderByIdDesc())
                .isPresent()
                .get()
                .extracting(Block::getHash)
                .isEqualTo("block-hash-1");
    }
}
