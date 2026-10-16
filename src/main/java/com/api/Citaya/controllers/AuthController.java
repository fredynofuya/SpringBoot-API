package com.api.Citaya.controllers;

import com.api.Citaya.dto.LoginRequest;
import com.api.Citaya.dto.LoginResponse;
import com.api.Citaya.models.MedicoModel;
import com.api.Citaya.models.UsuarioModel;
import com.api.Citaya.repositories.IMedicoRepository;
import com.api.Citaya.repositories.IUsuarioRepository;
import com.api.Citaya.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IMedicoRepository medicoRepository;

    private LoginResponse construirRespuesta(String token, UsuarioModel usuario) {
        Integer idMedico = null;
        if (usuario.getRol() == UsuarioModel.Rol.MEDICO) {
            idMedico = medicoRepository.buscarPorUsuario(usuario.getId())
                    .map(MedicoModel::getId)
                    .orElse(null);
        }
        return new LoginResponse(token, usuario.getId(), usuario.getNombre(),
                usuario.getEmail(), usuario.getRol().name(), idMedico);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }

        UsuarioModel usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));

        String token = jwtService.generarToken(usuario.getEmail(), usuario.getRol().name());

        return construirRespuesta(token, usuario);
    }

    // El frontend lo usa al recargar la página para validar el token guardado
    // y recuperar los datos del usuario sin pedirle que vuelva a loguearse.

    @GetMapping("/me")
    public LoginResponse me(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }

        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extraerEmail(token);

        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Token inválido"));

        return construirRespuesta(token, usuario);
    }

}