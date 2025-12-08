package com.myorg.tracemed.repository;

import com.myorg.tracemed.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    // Find the latest block to chain the next one
    Optional<Block> findTopByOrderByIdDesc();
}
