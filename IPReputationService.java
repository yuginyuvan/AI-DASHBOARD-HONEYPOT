package com.honeypot.backend.service;

import com.honeypot.backend.model.IPReputation;
import com.honeypot.backend.repository.IPReputationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IPReputationService {

    private final IPReputationRepository ipReputationRepository;

    public IPReputationService(IPReputationRepository ipReputationRepository) {
        this.ipReputationRepository = ipReputationRepository;
    }

    public List<IPReputation> getAllReputations() {
        return ipReputationRepository.findAll();
    }

    public Optional<IPReputation> getReputationByIp(String ipAddress) {
        return ipReputationRepository.findByIpAddress(ipAddress);
    }

    public IPReputation saveOrUpdate(IPReputation reputation) {
        if (reputation.getLastChecked() == null) {
            reputation.setLastChecked(LocalDateTime.now());
        }
        return ipReputationRepository.save(reputation);
    }
}
