package com.honeypot.backend.service;

import com.honeypot.backend.model.*;
import com.honeypot.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AttackSessionService {

    private final AttackSessionRepository attackSessionRepository;
    private final CommandLogRepository commandLogRepository;
    private final CredentialAttemptRepository credentialAttemptRepository;
    private final DownloadedFileRepository downloadedFileRepository;
    private final AIAnalysisRepository aiAnalysisRepository;

    public AttackSessionService(
            AttackSessionRepository attackSessionRepository,
            CommandLogRepository commandLogRepository,
            CredentialAttemptRepository credentialAttemptRepository,
            DownloadedFileRepository downloadedFileRepository,
            AIAnalysisRepository aiAnalysisRepository
    ) {
        this.attackSessionRepository = attackSessionRepository;
        this.commandLogRepository = commandLogRepository;
        this.credentialAttemptRepository = credentialAttemptRepository;
        this.downloadedFileRepository = downloadedFileRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
    }

    public List<AttackSession> getAllSessions() {
        return attackSessionRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AttackSession> getRecentSessions() {
        return attackSessionRepository.findTop20ByOrderByCreatedAtDesc();
    }

    public Optional<AttackSession> getSessionById(Integer sessionId) {
        return attackSessionRepository.findById(sessionId);
    }

    public Map<String, Object> getSessionFullDetails(Integer sessionId) {
        Optional<AttackSession> sessionOpt = attackSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return Map.of("error", "Session not found");
        }

        AttackSession session = sessionOpt.get();
        List<CommandLog> commands = commandLogRepository.findBySession_SessionId(sessionId);
        List<CredentialAttempt> credentials = credentialAttemptRepository.findBySession_SessionId(sessionId);
        List<DownloadedFile> downloads = downloadedFileRepository.findBySession_SessionId(sessionId);

        Map<String, Object> details = new HashMap<>();
        details.put("session", session);
        details.put("commands", commands);
        details.put("credentials", credentials);
        details.put("downloads", downloads);
        return details;
    }

    public void deleteSession(Integer sessionId) {
        attackSessionRepository.deleteById(sessionId);
    }
}
