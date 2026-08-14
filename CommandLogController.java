package com.honeypot.backend.controller;

import com.honeypot.backend.model.CommandLog;
import com.honeypot.backend.service.CommandLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attacks/commands")
@CrossOrigin(origins = "*")
public class CommandLogController {

    private final CommandLogService commandLogService;

    public CommandLogController(CommandLogService commandLogService) {
        this.commandLogService = commandLogService;
    }

    @GetMapping
    public ResponseEntity<List<CommandLog>> getAllCommands() {
        return ResponseEntity.ok(commandLogService.getAllCommands());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<CommandLog>> getRecentCommands() {
        return ResponseEntity.ok(commandLogService.getRecentCommands());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<CommandLog>> getCommandsBySession(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(commandLogService.getCommandsBySession(sessionId));
    }
}
