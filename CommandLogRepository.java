package com.honeypot.backend.repository;

import com.honeypot.backend.model.CommandLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandLogRepository extends JpaRepository<CommandLog, Integer> {

    List<CommandLog> findAllByOrderByCommandTimeDesc();

    List<CommandLog> findTop50ByOrderByCommandTimeDesc();

    List<CommandLog> findBySession_SessionId(Integer sessionId);
}