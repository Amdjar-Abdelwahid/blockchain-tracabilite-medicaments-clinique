package com.myorg.tracemed.repository;

import com.myorg.tracemed.entity.EvenementColis;
import com.myorg.tracemed.entity.ColisPhysique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvenementColisRepository extends JpaRepository<EvenementColis, Long> {
    List<EvenementColis> findByColisPhysiqueOrderByNumeroSequenceAsc(ColisPhysique colis);

    Long countByColisPhysique(ColisPhysique colis);

    Optional<EvenementColis> findByColisPhysiqueAndNumeroSequence(ColisPhysique colis, Long numeroSequence);
}
