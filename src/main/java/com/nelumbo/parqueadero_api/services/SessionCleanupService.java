package com.nelumbo.parqueadero_api.services;


import com.nelumbo.parqueadero_api.repository.UserSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
class SessionCleanupService {
    private final UserSessionRepository userSessionRepository;

    @Scheduled(fixedRate = 3600000) // Ejecuta cada hora (en milisegundos)
    @Transactional
    public void cleanupExpiredSessions() {
        userSessionRepository.deleteExpiredSessions(new Date());
    }
}