package com.myorg.tracemed.repository;

import com.myorg.tracemed.entity.ColisPhysique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColisPhysiqueRepository extends JpaRepository<ColisPhysique, Long> {
    Optional<ColisPhysique> findByIdentifiantColis(String identifiantColis);
}
