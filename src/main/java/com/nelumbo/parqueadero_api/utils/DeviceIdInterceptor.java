package com.nelumbo.parqueadero_api.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DeviceIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String deviceId = request.getHeader("deviceId");

        if (deviceId == null || deviceId.trim().isEmpty()) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Device ID es requerido");
            return false;
        }

        // Puedes también asociarlo a la sesión del usuario aquí si quieres
        return true;
    }
}
