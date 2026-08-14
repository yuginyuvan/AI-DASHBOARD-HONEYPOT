package com.honeypot.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "command_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CommandLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "command_id")
    private Integer commandId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private AttackSession session;

    @Column(columnDefinition = "TEXT")
    private String command;

    @Column(name = "working_directory")
    private String workingDirectory;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "command_time")
    private LocalDateTime commandTime;
}