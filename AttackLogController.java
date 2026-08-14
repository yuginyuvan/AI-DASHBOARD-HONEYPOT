package com.honeypot.backend.controller;

import com.honeypot.backend.model.AttackLog;
import com.honeypot.backend.service.AttackProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class AttackLogController {

    private final AttackProcessingService attackProcessingService;

    public AttackLogController(AttackProcessingService attackProcessingService) {
        this.attackProcessingService = attackProcessingService;
    }

    @PostMapping
    public ResponseEntity<AttackLog> saveLog(@RequestBody AttackLog log) {
        AttackLog saved = attackProcessingService.processAttack(log);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<AttackLog>> getLogs() {
        return ResponseEntity.ok(attackProcessingService.getAllLogs());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AttackLog>> getRecentLogs() {
        return ResponseEntity.ok(attackProcessingService.getRecentLogs());
    }
}