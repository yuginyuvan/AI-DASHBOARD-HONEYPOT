package com.honeypot.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ip_reputation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IPReputation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reputation_id")
    private Integer reputationId;

    @Column(name = "ip_address", unique = true)
    private String ipAddress;

    private String country;

    private String city;

    private String asn;

    private String organization;

    @Column(name = "blacklist_status")
    private Boolean blacklistStatus;

    private Boolean vpn;

    private Boolean tor;

    @Column(name = "reputation_score")
    private Integer reputationScore;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;

}