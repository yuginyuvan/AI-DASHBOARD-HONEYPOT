package com.honeypot.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="attack_logs")
@Data
public class AttackLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String eventId;

    private String username;

    private String password;

    private String sourceIp;

    private String protocol;

    private String commandInput;

    private LocalDateTime timestamp;

}