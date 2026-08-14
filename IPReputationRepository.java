package com.honeypot.backend.repository;

import com.honeypot.backend.model.IPReputation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPReputationRepository extends JpaRepository<IPReputation, Integer> {

    Optional<IPReputation> findByIpAddress(String ipAddress);

}