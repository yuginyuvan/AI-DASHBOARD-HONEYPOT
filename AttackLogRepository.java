package com.honeypot.backend.repository;

import com.honeypot.backend.model.AttackLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttackLogRepository
extends JpaRepository<AttackLog, Long> {

}