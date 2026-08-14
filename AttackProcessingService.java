package com.honeypot.backend.service;

import com.honeypot.backend.dto.CowrieEventDTO;
import com.honeypot.backend.model.*;
import com.honeypot.backend.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AttackProcessingService {

    private final AttackLogRepository attackLogRepository;
    private final AttackSessionRepository attackSessionRepository;
    private final AttackEventRepository attackEventRepository;
    private final CommandLogRepository commandLogRepository;
    private final CredentialAttemptRepository credentialAttemptRepository;
    private final DownloadedFileRepository downloadedFileRepository;
    private final AIAnalysisRepository aiAnalysisRepository;
    private final RestClient restClient;

    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    public AttackProcessingService(
            AttackLogRepository attackLogRepository,
            AttackSessionRepository attackSessionRepository,
            AttackEventRepository attackEventRepository,
            CommandLogRepository commandLogRepository,
            CredentialAttemptRepository credentialAttemptRepository,
            DownloadedFileRepository downloadedFileRepository,
            AIAnalysisRepository aiAnalysisRepository
    ) {
        this.attackLogRepository = attackLogRepository;
        this.attackSessionRepository = attackSessionRepository;
        this.attackEventRepository = attackEventRepository;
        this.commandLogRepository = commandLogRepository;
        this.credentialAttemptRepository = credentialAttemptRepository;
        this.downloadedFileRepository = downloadedFileRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.restClient = RestClient.builder().build();
    }

    @Transactional
    public AttackLog processAttack(AttackLog log) {
        if (log.getTimestamp() == null) {
            log.setTimestamp(LocalDateTime.now());
        }
        AttackLog savedLog = attackLogRepository.save(log);

        AttackEvent event = new AttackEvent();
        event.setEventType(log.getEventId() != null ? log.getEventId() : "ATTACK_LOG");
        event.setEventTimestamp(log.getTimestamp());
        event.setMessage(log.getMessage() != null ? log.getMessage() : log.getCommandInput());
        event.setRawJson(log.getRawJson());
        attackEventRepository.save(event);

        if (log.getSessionId() != null && !log.getSessionId().isEmpty()) {
            AttackSession session = getOrCreateSession(log.getSessionId(), log.getSourceIp(), log.getProtocol());
            if (log.getUsername() != null || log.getPassword() != null) {
                CredentialAttempt cred = new CredentialAttempt();
                cred.setSession(session);
                cred.setUsername(log.getUsername());
                cred.setPassword(log.getPassword());
                cred.setSuccess(false);
                cred.setAttemptTime(log.getTimestamp());
                credentialAttemptRepository.save(cred);
            }
            if (log.getCommandInput() != null && !log.getCommandInput().trim().isEmpty()) {
                saveCommandAndAnalyze(session, log.getCommandInput(), log.getTimestamp());
            }
        }

        return savedLog;
    }

    @Transactional
    public AttackSession processCowrieEvent(CowrieEventDTO dto) {
        LocalDateTime now = parseTimestamp(dto.getTimestamp());
        String sessionKey = dto.getSession() != null ? dto.getSession() : "sess_" + System.currentTimeMillis();

        AttackSession session = attackSessionRepository.findByCowrieSessionId(sessionKey)
                .orElseGet(() -> {
                    AttackSession s = new AttackSession();
                    s.setCowrieSessionId(sessionKey);
                    s.setSourceIp(dto.getSrcIp() != null ? dto.getSrcIp() : "127.0.0.1");
                    s.setSourcePort(dto.getSrcPort() != null ? dto.getSrcPort() : 22);
                    s.setDestinationIp(dto.getDstIp() != null ? dto.getDstIp() : "0.0.0.0");
                    s.setDestinationPort(dto.getDstPort() != null ? dto.getDstPort() : 2222);
                    s.setProtocol(dto.getProtocol() != null ? dto.getProtocol().toUpperCase() : "SSH");
                    s.setCountry(dto.getCountry() != null ? dto.getCountry() : "US");
                    s.setCity(dto.getCity() != null ? dto.getCity() : "Unknown");
                    s.setLoginStatus("ATTEMPTED");
                    s.setLoginTime(now);
                    s.setCreatedAt(LocalDateTime.now());
                    return attackSessionRepository.save(s);
                });

        String eventId = dto.getEventId() != null ? dto.getEventId() : "";

        // 1. Save general AttackEvent
        AttackEvent event = new AttackEvent();
        event.setSessionId(session.getSessionId());
        event.setEventType(eventId);
        event.setEventTimestamp(now);
        event.setMessage(dto.getMessage() != null ? dto.getMessage() : dto.getInput());
        attackEventRepository.save(event);

        // 2. Process specific Cowrie events
        if (eventId.contains("login.success")) {
            session.setUsername(dto.getUsername());
            session.setPassword(dto.getPassword());
            session.setLoginStatus("SUCCESS");
            attackSessionRepository.save(session);

            CredentialAttempt cred = new CredentialAttempt();
            cred.setSession(session);
            cred.setUsername(dto.getUsername());
            cred.setPassword(dto.getPassword());
            cred.setSuccess(true);
            cred.setAttemptTime(now);
            credentialAttemptRepository.save(cred);

        } else if (eventId.contains("login.failed")) {
            if (session.getUsername() == null) {
                session.setUsername(dto.getUsername());
                session.setPassword(dto.getPassword());
                session.setLoginStatus("FAILED");
                attackSessionRepository.save(session);
            }

            CredentialAttempt cred = new CredentialAttempt();
            cred.setSession(session);
            cred.setUsername(dto.getUsername());
            cred.setPassword(dto.getPassword());
            cred.setSuccess(false);
            cred.setAttemptTime(now);
            credentialAttemptRepository.save(cred);

        } else if (eventId.contains("command.input")) {
            saveCommandAndAnalyze(session, dto.getInput(), now);

        } else if (eventId.contains("file_download")) {
            DownloadedFile file = new DownloadedFile();
            file.setSession(session);
            file.setDownloadUrl(dto.getUrl());
            file.setFilename(dto.getOutfile() != null ? dto.getOutfile() : "payload.bin");
            file.setSha256Hash(dto.getSha256());
            file.setDownloadTime(now);
            downloadedFileRepository.save(file);

        } else if (eventId.contains("session.closed")) {
            session.setLogoutTime(now);
            if (dto.getDuration() != null) {
                session.setDurationSeconds(dto.getDuration().intValue());
            }
            attackSessionRepository.save(session);

            // Trigger full session AI Threat Analysis
            triggerSessionAIAnalysis(session);
        }

        // 3. Save raw AttackLog
        AttackLog rawLog = new AttackLog();
        rawLog.setEventId(eventId);
        rawLog.setSessionId(sessionKey);
        rawLog.setSourceIp(session.getSourceIp());
        rawLog.setSourcePort(session.getSourcePort());
        rawLog.setUsername(dto.getUsername() != null ? dto.getUsername() : session.getUsername());
        rawLog.setPassword(dto.getPassword() != null ? dto.getPassword() : session.getPassword());
        rawLog.setProtocol(session.getProtocol());
        rawLog.setCommandInput(dto.getInput());
        rawLog.setMessage(dto.getMessage() != null ? dto.getMessage() : eventId);
        rawLog.setTimestamp(now);
        attackLogRepository.save(rawLog);

        return session;
    }

    private void saveCommandAndAnalyze(AttackSession session, String commandInput, LocalDateTime timestamp) {
        if (commandInput == null || commandInput.trim().isEmpty()) return;

        CommandLog cmd = new CommandLog();
        cmd.setSession(session);
        cmd.setCommand(commandInput);
        cmd.setCommandTime(timestamp);

        // Call ML Service to classify command and get MITRE ATT&CK mapping
        Map<String, Object> aiResult = callMLClassifyCommand(commandInput);

        String riskLevel = (String) aiResult.getOrDefault("risk", evaluateFallbackRisk(commandInput));
        cmd.setRiskLevel(riskLevel);
        commandLogRepository.save(cmd);

        // Save / Update AIAnalysis in MySQL
        AIAnalysis analysis = new AIAnalysis();
        analysis.setSession(session);
        analysis.setAttackType((String) aiResult.getOrDefault("category", "Command Execution"));
        analysis.setSeverity(riskLevel);
        Object scoreObj = aiResult.get("score");
        int score = (scoreObj instanceof Number) ? ((Number) scoreObj).intValue() : 75;
        analysis.setRiskScore(score);
        analysis.setConfidence(0.94f);
        analysis.setAiSummary((String) aiResult.getOrDefault("mitre_tactic", "T1059 - Command Interpreter"));
        analysis.setRecommendedAction((String) aiResult.getOrDefault("recommendation", "Block attacker IP"));
        analysis.setAnalyzedAt(LocalDateTime.now());
        aiAnalysisRepository.save(analysis);
    }

    private Map<String, Object> callMLClassifyCommand(String command) {
        try {
            Map<String, String> body = Map.of("command", command);
            return restClient.post()
                    .uri(mlServiceUrl + "/api/ai/classify-command")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            // Graceful fallback if ML service is temporarily offline
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("risk", evaluateFallbackRisk(command));
            fallback.put("category", "Command Execution");
            fallback.put("mitre_tactic", "T1059 - Command and Scripting Interpreter");
            fallback.put("recommendation", "Block suspicious IP");
            fallback.put("score", 70);
            return fallback;
        }
    }

    private void triggerSessionAIAnalysis(AttackSession session) {
        try {
            List<CommandLog> commands = commandLogRepository.findBySession_SessionId(session.getSessionId());
            List<Map<String, String>> cmdList = new ArrayList<>();
            for (CommandLog c : commands) {
                cmdList.add(Map.of("command", c.getCommand()));
            }

            Map<String, Object> req = new HashMap<>();
            req.put("sourceIp", session.getSourceIp());
            req.put("commands", cmdList);
            req.put("failedLogins", 1);
            req.put("successfulLogins", "SUCCESS".equalsIgnoreCase(session.getLoginStatus()) ? 1 : 0);

            Map<String, Object> resp = restClient.post()
                    .uri(mlServiceUrl + "/api/ai/analyze-session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(req)
                    .retrieve()
                    .body(Map.class);

            if (resp != null) {
                AIAnalysis analysis = new AIAnalysis();
                analysis.setSession(session);
                analysis.setAttackType((String) resp.getOrDefault("attackType", "Brute Force Attack"));
                analysis.setSeverity((String) resp.getOrDefault("threatLevel", "HIGH"));
                Object scoreObj = resp.get("riskScore");
                int score = (scoreObj instanceof Number) ? (int)(((Number) scoreObj).doubleValue() * 10) : 85;
                analysis.setRiskScore(score);
                analysis.setConfidence(0.94f);
                analysis.setAiSummary((String) resp.getOrDefault("summary", "AI classified session"));
                List<?> recs = (List<?>) resp.get("recommendations");
                analysis.setRecommendedAction(recs != null && !recs.isEmpty() ? recs.get(0).toString() : "Block IP");
                analysis.setAnalyzedAt(LocalDateTime.now());
                aiAnalysisRepository.save(analysis);
            }
        } catch (Exception ignored) {
        }
    }

    private AttackSession getOrCreateSession(String sessionId, String ip, String protocol) {
        return attackSessionRepository.findByCowrieSessionId(sessionId)
                .orElseGet(() -> {
                    AttackSession s = new AttackSession();
                    s.setCowrieSessionId(sessionId);
                    s.setSourceIp(ip != null ? ip : "127.0.0.1");
                    s.setProtocol(protocol != null ? protocol : "SSH");
                    s.setLoginStatus("ATTEMPTED");
                    s.setCreatedAt(LocalDateTime.now());
                    return attackSessionRepository.save(s);
                });
    }

    public List<AttackLog> getAllLogs() {
        return attackLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AttackLog> getRecentLogs() {
        return attackLogRepository.findTop50ByOrderByTimestampDesc();
    }

    private String evaluateFallbackRisk(String command) {
        if (command == null) return "LOW";
        String lower = command.toLowerCase();
        if (lower.contains("rm -rf") || lower.contains("curl") || lower.contains("wget") ||
                lower.contains("bash -i") || lower.contains("nc -e") || lower.contains("chmod +x") ||
                lower.contains("/etc/shadow") || lower.contains("dd if=")) {
            return "HIGH";
        }
        if (lower.contains("sudo") || lower.contains("cat /etc/passwd") || lower.contains("uname -a") ||
                lower.contains("iptables") || lower.contains("systemctl") || lower.contains("netstat")) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private LocalDateTime parseTimestamp(String ts) {
        if (ts == null || ts.trim().isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(ts, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}