package com.myorg.tracemed.repository;

import com.myorg.tracemed.entity.TransactionBlockchain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionBlockchainRepository extends JpaRepository<TransactionBlockchain, Long> {
    Optional<TransactionBlockchain> findByHashTx(String hashTx);

    java.util.List<TransactionBlockchain> findByBlockIsNull();
}
