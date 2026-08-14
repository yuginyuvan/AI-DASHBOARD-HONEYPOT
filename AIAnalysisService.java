package com.honeypot.backend.service;

import com.honeypot.backend.model.AIAnalysis;
import com.honeypot.backend.model.AttackSession;
import com.honeypot.backend.repository.AIAnalysisRepository;
import com.honeypot.backend.repository.AttackSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AIAnalysisService {

    private final AIAnalysisRepository aiAnalysisRepository;
    private final AttackSessionRepository attackSessionRepository;

    public AIAnalysisService(AIAnalysisRepository aiAnalysisRepository, AttackSessionRepository attackSessionRepository) {
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.attackSessionRepository = attackSessionRepository;
    }

    public List<AIAnalysis> getAllAnalyses() {
        return aiAnalysisRepository.findAll();
    }

    public Optional<AIAnalysis> getAnalysisBySessionId(Integer sessionId) {
        return aiAnalysisRepository.findById(sessionId);
    }

    public AIAnalysis saveAnalysis(AIAnalysis analysis) {
        if (analysis.getAnalyzedAt() == null) {
            analysis.setAnalyzedAt(LocalDateTime.now());
        }
        return aiAnalysisRepository.save(analysis);
    }
}
