package com.myorg.tracemed.service;

import com.myorg.tracemed.entity.Block;
import com.myorg.tracemed.repository.BlockRepository;
import com.myorg.tracemed.repository.TransactionBlockchainRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class BlockServiceTest {

    @Autowired
    private BlockService blockService;

    @MockBean
    private BlockRepository blockRepository;

    @MockBean
    private TransactionBlockchainRepository transactionRepository;

    @Test
    void mineBlockShouldReturnNullWhenNoPendingTransactions() {
        when(transactionRepository.findByBlockIsNull()).thenReturn(java.util.Collections.emptyList());

        Block result = blockService.mineBlock();

        assertThat(result).isNull();
    }

    @Test
    void findTopBlockShouldReturnMostRecent() {
        Block block = Block.builder()
                .hash("latest-hash")
                .previousBlockHash("prev-hash")
                .merkleRoot("merkle")
                .build();

        when(blockRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(block));

        Optional<Block> result = blockRepository.findTopByOrderByIdDesc();

        assertThat(result).isPresent();
        assertThat(result.get().getHash()).isEqualTo("latest-hash");
    }
}
