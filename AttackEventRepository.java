package com.honeypot.backend.repository;

import com.honeypot.backend.model.AttackEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttackEventRepository extends JpaRepository<AttackEvent, Integer> {

}