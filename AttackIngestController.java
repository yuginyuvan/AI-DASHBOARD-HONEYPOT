package com.honeypot.backend.controller;

import com.honeypot.backend.dto.CowrieEventDTO;
import com.honeypot.backend.model.AttackLog;
import com.honeypot.backend.model.AttackSession;
import com.honeypot.backend.service.AttackProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attacks")
@CrossOrigin(origins = "*")
public class AttackIngestController {

    private final AttackProcessingService attackProcessingService;

    public AttackIngestController(AttackProcessingService attackProcessingService) {
        this.attackProcessingService = attackProcessingService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<AttackSession> ingestCowrieEvent(@RequestBody CowrieEventDTO event) {
        AttackSession session = attackProcessingService.processCowrieEvent(event);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AttackLog>> getRecentAttacks() {
        return ResponseEntity.ok(attackProcessingService.getRecentLogs());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> map = new HashMap<>();
        map.put("service", "attack-service");
        map.put("status", "UP");
        return ResponseEntity.ok(map);
    }
}
