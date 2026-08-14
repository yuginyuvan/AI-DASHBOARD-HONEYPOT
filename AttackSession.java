package com.honeypot.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "attack_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;

    @Column(name = "cowrie_session_id", unique = true)
    private String cowrieSessionId;

    @Column(name = "source_ip")
    private String sourceIp;

    @Column(name = "source_port")
    private Integer sourcePort;

    @Column(name = "destination_ip")
    private String destinationIp;

    @Column(name = "destination_port")
    private Integer destinationPort;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "login_status")
    private String loginStatus;

    @Column(name = "login_time")
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "asn")
    private String asn;

    @Column(name = "organization")
    private String organization;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}