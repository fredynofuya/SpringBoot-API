package com.api.Citaya.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

// Única política de contraseña de la aplicación.
public final class PoliticaPassword {

    public static final int MIN = 8;
    private static final int MAX_BYTES = 72; // límite real de BCrypt

    private PoliticaPassword() {}

    public static void validar(String password) {
        if (password == null || password.length() < MIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos " + MIN + " caracteres");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es demasiado larga");
        }
    }
}