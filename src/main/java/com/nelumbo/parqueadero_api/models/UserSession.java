package com.nelumbo.parqueadero_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Table(name = "user_sessions", indexes = {
        @Index(name = "idx_session_username", columnList = "username"),
        @Index(name = "idx_session_device", columnList = "deviceId"),
        @Index(name = "idx_session_token", columnList = "token")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sessionId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;
}