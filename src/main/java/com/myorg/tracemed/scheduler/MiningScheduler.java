package com.myorg.tracemed.scheduler;

import com.myorg.tracemed.entity.Block;
import com.myorg.tracemed.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MiningScheduler {

    private final BlockService blockService;

    // Run every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void minePendingTransactions() {
        System.out.println("⛏️  Mining Scheduler: Checking for pending transactions...");
        Block b = blockService.mineBlock();
        if (b != null) {
            System.out.println("🧱 New Block Mined! ID=" + b.getId() + " Hash=" + b.getHash()
                    + " Transactions=" + b.getTransactions().size());
        } else {
            System.out.println("💤 No transactions to mine.");
        }
    }
}
