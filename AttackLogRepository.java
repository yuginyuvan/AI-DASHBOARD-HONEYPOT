package com.honeypot.backend.repository;

import com.honeypot.backend.model.AttackLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttackLogRepository extends JpaRepository<AttackLog, Long> {

    List<AttackLog> findAllByOrderByTimestampDesc();

    List<AttackLog> findTop100ByOrderByTimestampDesc();

    List<AttackLog> findTop50ByOrderByTimestampDesc();

    List<AttackLog> findTop20ByOrderByTimestampDesc();
}