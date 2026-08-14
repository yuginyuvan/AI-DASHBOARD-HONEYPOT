package com.honeypot.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "attack_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;

    @Column(name = "session_id")
    private Integer sessionId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "event_timestamp")
    private LocalDateTime eventTimestamp;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "raw_json", columnDefinition = "json")
    private String rawJson;

}