package com.myorg.tracemed.repository;

import com.myorg.tracemed.entity.TransactionBlockchain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionBlockchainRepository extends JpaRepository<TransactionBlockchain, Long> {}
