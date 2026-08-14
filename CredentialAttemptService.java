package com.honeypot.backend.service;

import com.honeypot.backend.model.CredentialAttempt;
import com.honeypot.backend.repository.CredentialAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CredentialAttemptService {

    private final CredentialAttemptRepository credentialAttemptRepository;

    public CredentialAttemptService(CredentialAttemptRepository credentialAttemptRepository) {
        this.credentialAttemptRepository = credentialAttemptRepository;
    }

    public List<CredentialAttempt> getAllAttempts() {
        return credentialAttemptRepository.findAllByOrderByAttemptTimeDesc();
    }

    public List<CredentialAttempt> getRecentAttempts() {
        return credentialAttemptRepository.findTop50ByOrderByAttemptTimeDesc();
    }

    public List<CredentialAttempt> getAttemptsBySession(Integer sessionId) {
        return credentialAttemptRepository.findBySession_SessionId(sessionId);
    }

    public List<Map<String, Object>> getTopCredentials() {
        List<Object[]> results = credentialAttemptRepository.findTopCredentialsGrouped();
        List<Map<String, Object>> list = new ArrayList<>();
        int limit = Math.min(results.size(), 10);
        for (int i = 0; i < limit; i++) {
            Object[] row = results.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("username", row[0] != null ? row[0].toString() : "empty");
            map.put("password", row[1] != null ? row[1].toString() : "empty");
            map.put("attempts", row[2]);
            map.put("status", "Weak");
            list.add(map);
        }
        return list;
    }
}
