package com.honeypot.backend.service;

import com.honeypot.backend.model.DownloadedFile;
import com.honeypot.backend.repository.DownloadedFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DownloadedFileService {

    private final DownloadedFileRepository downloadedFileRepository;

    public DownloadedFileService(DownloadedFileRepository downloadedFileRepository) {
        this.downloadedFileRepository = downloadedFileRepository;
    }

    public List<DownloadedFile> getAllDownloads() {
        return downloadedFileRepository.findAllByOrderByDownloadTimeDesc();
    }

    public List<DownloadedFile> getDownloadsBySession(Integer sessionId) {
        return downloadedFileRepository.findBySession_SessionId(sessionId);
    }
}
