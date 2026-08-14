package com.honeypot.backend.controller;

import com.honeypot.backend.model.AttackSession;
import com.honeypot.backend.service.AttackSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attacks/sessions")
@CrossOrigin(origins = "*")
public class AttackSessionController {

    private final AttackSessionService attackSessionService;

    public AttackSessionController(AttackSessionService attackSessionService) {
        this.attackSessionService = attackSessionService;
    }

    @GetMapping
    public ResponseEntity<List<AttackSession>> getAllSessions() {
        return ResponseEntity.ok(attackSessionService.getAllSessions());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AttackSession>> getRecentSessions() {
        return ResponseEntity.ok(attackSessionService.getRecentSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttackSession> getSessionById(@PathVariable Integer id) {
        return attackSessionService.getSessionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getSessionDetails(@PathVariable Integer id) {
        return ResponseEntity.ok(attackSessionService.getSessionFullDetails(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Integer id) {
        attackSessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
