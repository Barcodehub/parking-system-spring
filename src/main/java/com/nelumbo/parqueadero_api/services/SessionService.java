package com.nelumbo.parqueadero_api.services;

import com.nelumbo.parqueadero_api.models.UserSession;
import com.nelumbo.parqueadero_api.repository.UserSessionRepository;
import com.nelumbo.parqueadero_api.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserSessionRepository userSessionRepository;
    private final UserSessionRepository sessionRepository;


    private final JwtService jwtService;

    public UserSession createSession(String username, String deviceId, String token) {

        // Invalidar sesión previa si existe
        userSessionRepository.findByUsernameAndDeviceId(username, deviceId)
                .ifPresent(userSessionRepository::delete);

        UserSession session = new UserSession();
        session.setUsername(username);
        session.setDeviceId(deviceId);
        session.setToken(token);
        session.setCreatedAt(new Date());
        session.setExpiresAt(jwtService.extractExpiration(token));

        return userSessionRepository.save(session);
    }


    public void invalidateAllSessionsForUser(String username) {
        userSessionRepository.deleteByUsername(username);
    }

    @Transactional
    public void invalidateSessionForDevice(String username, String deviceId) {
        userSessionRepository.deleteByUsernameAndDeviceId(username, deviceId);
    }

    public boolean isSessionValid(String token, String deviceId) {
        // Primero verifica si el token JWT está expirado
        if (jwtService.isTokenExpired(token)) {
            return false;
        }

        // Luego verifica la sesión en la base de datos
        return userSessionRepository.existsByTokenAndDeviceIdAndExpiresAtAfter(
                token,
                deviceId
        );
    }
    public boolean hasActiveSession(String username, String deviceId) {
        return sessionRepository.existsByUsernameAndDeviceId(username, deviceId);

    }
    public List<UserSession> getActiveSessions(String username) {
        return userSessionRepository.findByUsernameAndExpiresAtAfter(username, new Date());
    }
}
