package com.myorg.tracemed.repository;

import com.myorg.tracemed.entity.DemandeTransfert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeTransfertRepository extends JpaRepository<DemandeTransfert, Long> {

    List<DemandeTransfert> findByStatut(String statut);

    List<DemandeTransfert> findByDemandePar_Username(String username);

    List<DemandeTransfert> findByOrgSource_Id(Long id);

    List<DemandeTransfert> findByOrgDestination_Id(Long id);
}
