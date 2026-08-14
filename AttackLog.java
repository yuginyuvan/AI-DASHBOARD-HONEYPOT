package com.honeypot.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "attack_logs")
@Data
public class AttackLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "session_uuid")
    private String sessionId;

    private String username;

    private String password;

    @Column(name = "source_ip")
    private String sourceIp;

    @Column(name = "source_port")
    private Integer sourcePort;

    @Column(name = "destination_ip")
    private String destinationIp;

    @Column(name = "destination_port")
    private Integer destinationPort;

    private String protocol;

    @Column(name = "command_input")
    private String commandInput;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "raw_json", columnDefinition = "json")
    private String rawJson;

    @Column(name = "event_timestamp")
    private LocalDateTime timestamp;
}