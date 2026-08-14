package com.honeypot.backend.repository;

import com.honeypot.backend.model.DownloadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownloadedFileRepository extends JpaRepository<DownloadedFile, Integer> {

    List<DownloadedFile> findAllByOrderByDownloadTimeDesc();

    List<DownloadedFile> findBySession_SessionId(Integer sessionId);
}