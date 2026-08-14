package com.honeypot.backend.controller;

import com.honeypot.backend.model.CredentialAttempt;
import com.honeypot.backend.service.CredentialAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attacks/credentials")
@CrossOrigin(origins = "*")
public class CredentialAttemptController {

    private final CredentialAttemptService credentialAttemptService;

    public CredentialAttemptController(CredentialAttemptService credentialAttemptService) {
        this.credentialAttemptService = credentialAttemptService;
    }

    @GetMapping
    public ResponseEntity<List<CredentialAttempt>> getAllCredentials() {
        return ResponseEntity.ok(credentialAttemptService.getAllAttempts());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<CredentialAttempt>> getRecentCredentials() {
        return ResponseEntity.ok(credentialAttemptService.getRecentAttempts());
    }

    @GetMapping("/top")
    public ResponseEntity<List<Map<String, Object>>> getTopCredentials() {
        return ResponseEntity.ok(credentialAttemptService.getTopCredentials());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<CredentialAttempt>> getCredentialsBySession(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(credentialAttemptService.getAttemptsBySession(sessionId));
    }
}
