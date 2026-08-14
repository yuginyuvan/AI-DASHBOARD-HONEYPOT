package com.honeypot.backend.repository;

import com.honeypot.backend.model.AIAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIAnalysisRepository extends JpaRepository<AIAnalysis, Integer> {

}