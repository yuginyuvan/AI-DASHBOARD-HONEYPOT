package com.honeypot.backend.controller;

import com.honeypot.backend.model.AttackLog;
import com.honeypot.backend.repository.AttackLogRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/logs")
@CrossOrigin("*")
public class AttackLogController {


    private final AttackLogRepository repo;


    public AttackLogController(AttackLogRepository repo){
        this.repo = repo;
    }


    @PostMapping
    public AttackLog saveLog(
            @RequestBody AttackLog log
    ){

        return repo.save(log);

    }


    @GetMapping
    public List<AttackLog> getLogs(){

        return repo.findAll();

    }

}