package com.myorg.tracemed.repository;

import com.myorg.tracemed.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {}
