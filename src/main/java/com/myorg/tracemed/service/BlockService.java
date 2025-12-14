package com.myorg.tracemed.service;

import com.myorg.tracemed.entity.Block;
import com.myorg.tracemed.entity.TransactionBlockchain;
import com.myorg.tracemed.repository.BlockRepository;
import com.myorg.tracemed.repository.TransactionBlockchainRepository;
import com.myorg.tracemed.util.HashUtil;
import com.myorg.tracemed.util.MerkleTreeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepo;
    private final TransactionBlockchainRepository txRepo;
    private final FileLedgerService fileLedgerService;

    /**
     * "Mines" a new block by gathering all unconfirmed transactions.
     * 
     * @return The new Block or null if no transactions pending.
     */
    @Transactional
    public Block mineBlock() {
        // 1. Get unconfirmed transactions (where block is null)
        List<TransactionBlockchain> pendingTxs = txRepo.findByBlockIsNull();

        if (pendingTxs.isEmpty()) {
            return null; // Nothing to mine
        }

        // 2. Calculate Merkle Root
        List<String> txHashes = pendingTxs.stream()
                .map(TransactionBlockchain::getHashTx)
                .collect(Collectors.toList());

        String merkleRoot = MerkleTreeUtil.calculateMerkleRoot(txHashes);

        // 3. Get Previous Block Hash
        String previousHash = "0000000000000000000000000000000000000000000000000000000000000000";
        Optional<Block> lastBlock = blockRepo.findTopByOrderByIdDesc();
        if (lastBlock.isPresent()) {
            previousHash = lastBlock.get().getHash();
        }

        // 4. Create Block Header and Hash
        Instant now = Instant.now();
        // Simple Proof of Work placeholder (nonce = 0)
        long nonce = 0;
        String blockHeader = previousHash + merkleRoot + now.toString() + nonce;
        String blockHash = HashUtil.sha256Hex(blockHeader);

        Block newBlock = Block.builder()
                .previousBlockHash(previousHash)
                .merkleRoot(merkleRoot)
                .timestamp(now)
                .nonce(nonce)
                .hash(blockHash)
                .build();

        // Save block first
        newBlock = blockRepo.save(newBlock);

        // 5. Update Transactions
        for (TransactionBlockchain tx : pendingTxs) {
            tx.setBlock(newBlock);
            tx.setNumeroBloc(newBlock.getId()); // Sync legacy field if used
            // dateBloc ??
            tx.setDateBloc(now);
        }
        txRepo.saveAll(pendingTxs);

        // 6. Write to File Ledger
        fileLedgerService.writeBlock(newBlock);

        // Refresh block to have transactions list if needed, or return as is
        return newBlock;
    }
}
