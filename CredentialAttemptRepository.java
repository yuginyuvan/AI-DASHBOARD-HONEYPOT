package com.honeypot.backend.repository;

import com.honeypot.backend.model.CredentialAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialAttemptRepository extends JpaRepository<CredentialAttempt, Integer> {

    List<CredentialAttempt> findAllByOrderByAttemptTimeDesc();

    List<CredentialAttempt> findTop50ByOrderByAttemptTimeDesc();

    List<CredentialAttempt> findBySession_SessionId(Integer sessionId);

    @Query("SELECT c.username, c.password, COUNT(c) as cnt FROM CredentialAttempt c GROUP BY c.username, c.password ORDER BY cnt DESC")
    List<Object[]> findTopCredentialsGrouped();
}