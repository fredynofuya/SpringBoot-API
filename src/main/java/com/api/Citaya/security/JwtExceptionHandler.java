package com.api.Citaya.security;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestControllerAdvice
public class JwtExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean anonimo = auth == null || auth instanceof AnonymousAuthenticationToken;

        if (anonimo) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión expirada o no autenticada");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para realizar esta acción");
    }

//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para realizar esta acción");
//    }
}