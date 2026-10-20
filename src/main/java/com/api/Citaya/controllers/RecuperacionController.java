package com.api.Citaya.controllers;

import com.api.Citaya.dto.PasswordDTO;
import com.api.Citaya.services.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth/recuperar")
public class RecuperacionController {

    @Autowired
    private PasswordService passwordService;

    // Respuesta idéntica exista o no el correo.
    @PostMapping("/solicitar")
    public Map<String, String> solicitar(@RequestBody PasswordDTO.Solicitud request) {
        passwordService.solicitarRecuperacion(request.email());
        return Map.of("mensaje", "Si el correo está registrado, te enviamos las instrucciones.");
    }

    @PostMapping("/validar")
    public Map<String, Boolean> validar(@RequestBody PasswordDTO.Token request) {
        if (!passwordService.tokenValido(request.token())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El enlace no es válido o ya venció");
        }
        return Map.of("valido", true);
    }

    @PostMapping("/restablecer")
    public Map<String, String> restablecer(@RequestBody PasswordDTO.Restablecer request) {
        passwordService.restablecer(request.token(), request.passwordNueva());
        return Map.of("mensaje", "Contraseña actualizada. Ya puedes iniciar sesión.");
    }
}