package com.nelumbo.parqueadero_api.repository;


import com.nelumbo.parqueadero_api.models.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {

    // Encontrar sesión por nombre de usuario y deviceId
    Optional<UserSession> findByUsernameAndDeviceId(String username, String deviceId);

    // Eliminar sesiones por nombre de usuario y deviceId
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.username = :username AND s.deviceId = :deviceId")
    void deleteByUsernameAndDeviceId(@Param("username") String username,
                                     @Param("deviceId") String deviceId);

    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :currentDate")
    void deleteExpiredSessions(@Param("currentDate") Date currentDate);

    // Verificar existencia de sesión activa
    @Query("SELECT COUNT(s) > 0 FROM UserSession s WHERE s.token = :token AND s.deviceId = :deviceId AND s.expiresAt > CURRENT_TIMESTAMP")
    boolean existsByTokenAndDeviceIdAndExpiresAtAfter(@Param("token") String token,
                                                      @Param("deviceId") String deviceId);

    // Obtener todas las sesiones activas de un usuario
    List<UserSession> findByUsernameAndExpiresAtAfter(String username, Date date);

    boolean existsByUsernameAndDeviceId(String username, String deviceId);

    // Eliminar todas las sesiones de un usuario
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.username = :username")
    void deleteByUsername(@Param("username") String username);
}
