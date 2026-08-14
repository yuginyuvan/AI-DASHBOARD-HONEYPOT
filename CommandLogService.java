package com.honeypot.backend.service;

import com.honeypot.backend.model.CommandLog;
import com.honeypot.backend.repository.CommandLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandLogService {

    private final CommandLogRepository commandLogRepository;

    public CommandLogService(CommandLogRepository commandLogRepository) {
        this.commandLogRepository = commandLogRepository;
    }

    public List<CommandLog> getAllCommands() {
        return commandLogRepository.findAllByOrderByCommandTimeDesc();
    }

    public List<CommandLog> getRecentCommands() {
        return commandLogRepository.findTop50ByOrderByCommandTimeDesc();
    }

    public List<CommandLog> getCommandsBySession(Integer sessionId) {
        return commandLogRepository.findBySession_SessionId(sessionId);
    }
}
