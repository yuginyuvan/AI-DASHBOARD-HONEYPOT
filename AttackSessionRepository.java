package com.honeypot.backend.repository;

import com.honeypot.backend.model.AttackSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttackSessionRepository extends JpaRepository<AttackSession, Integer> {

    Optional<AttackSession> findByCowrieSessionId(String cowrieSessionId);

    List<AttackSession> findAllByOrderByCreatedAtDesc();

    List<AttackSession> findTop20ByOrderByCreatedAtDesc();

    long countByLoginStatus(String loginStatus);

    List<AttackSession> findBySourceIp(String sourceIp);
}