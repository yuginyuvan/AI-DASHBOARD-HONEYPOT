package com.honeypot.backend.controller;

import com.honeypot.backend.model.DownloadedFile;
import com.honeypot.backend.service.DownloadedFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attacks/downloads")
@CrossOrigin(origins = "*")
public class DownloadedFileController {

    private final DownloadedFileService downloadedFileService;

    public DownloadedFileController(DownloadedFileService downloadedFileService) {
        this.downloadedFileService = downloadedFileService;
    }

    @GetMapping
    public ResponseEntity<List<DownloadedFile>> getAllDownloads() {
        return ResponseEntity.ok(downloadedFileService.getAllDownloads());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<DownloadedFile>> getDownloadsBySession(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(downloadedFileService.getDownloadsBySession(sessionId));
    }
}
