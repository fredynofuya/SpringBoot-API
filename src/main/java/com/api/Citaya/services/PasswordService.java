package com.api.Citaya.services;

import com.api.Citaya.correo.EmailService;
import com.api.Citaya.models.PasswordResetTokenModel;
import com.api.Citaya.models.UsuarioModel;
import com.api.Citaya.repositories.IPasswordResetTokenRepository;
import com.api.Citaya.repositories.IUsuarioRepository;
import com.api.Citaya.security.PoliticaPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordService {

    private static final String ENLACE_INVALIDO = "El enlace no es válido o ya venció";

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.recuperacion.expiracion-minutos}")
    private int expiracionMinutos;

    @Value("${app.recuperacion.espera-segundos}")
    private int esperaSegundos;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IPasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final SecureRandom random = new SecureRandom();

    /* ---------- Fase 3: cambiar mi contraseña (con sesión) ---------- */

    @Transactional
    public UsuarioModel cambiar(int idUsuario, String actual, String nueva) {
        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión requerida"));

        // 400 y no 401: el interceptor del frontend cierra la sesión ante un 401.
        if (actual == null || !passwordEncoder.matches(actual, usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
        }
        PoliticaPassword.validar(nueva);
        if (passwordEncoder.matches(nueva, usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La nueva contraseña debe ser distinta de la actual");
        }

        aplicarNuevaPassword(usuario, nueva);
        return usuario;
    }

    /* ---------- Fase 4: recuperación por correo (sin sesión) ---------- */

    // Nunca revela si el correo existe: ante cualquier caso responde igual.
    @Transactional
    public void solicitarRecuperacion(String email) {
        if (email == null || email.isBlank()) return;

        Optional<UsuarioModel> encontrado = usuarioRepository.findByEmail(email.trim());
        if (encontrado.isEmpty() || !Boolean.TRUE.equals(encontrado.get().getActivo())) return;
        UsuarioModel usuario = encontrado.get();

        LocalDateTime ahora = LocalDateTime.now();
        List<PasswordResetTokenModel> pendientes = tokenRepository.findByUsuarioIdAndUsadoEnIsNull(usuario.getId());

        // Límite: un solo envío por usuario cada N segundos
        boolean enEspera = pendientes.stream()
                .anyMatch(t -> t.getCreadoEn().isAfter(ahora.minusSeconds(esperaSegundos)));
        if (enEspera) return;

        // Un solo enlace activo por usuario: los anteriores quedan invalidados
        pendientes.forEach(t -> t.setUsadoEn(ahora));
        tokenRepository.saveAll(pendientes);

        String tokenPlano = generarTokenPlano();
        PasswordResetTokenModel token = new PasswordResetTokenModel();
        token.setUsuarioId(usuario.getId());
        token.setTokenHash(hash(tokenPlano));
        token.setCreadoEn(ahora);
        token.setExpiraEn(ahora.plusMinutes(expiracionMinutos));
        tokenRepository.save(token);

        String enlace = frontendUrl + "/restablecer-contrasena?token=" + tokenPlano;
        emailService.enviarRecuperacion(usuario.getEmail(), usuario.getNombre(), enlace, expiracionMinutos);
    }

    @Transactional(readOnly = true)
    public boolean tokenValido(String tokenPlano) {
        return buscarVigente(tokenPlano).isPresent();
    }

    @Transactional
    public void restablecer(String tokenPlano, String nueva) {
        PasswordResetTokenModel token = buscarVigente(tokenPlano)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ENLACE_INVALIDO));

        UsuarioModel usuario = usuarioRepository.findById(token.getUsuarioId())
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ENLACE_INVALIDO));

        PoliticaPassword.validar(nueva);

        token.setUsadoEn(LocalDateTime.now());
        tokenRepository.save(token);
        aplicarNuevaPassword(usuario, nueva);
    }

    /* ---------- Internos ---------- */

    private void aplicarNuevaPassword(UsuarioModel usuario, String nueva) {
        usuario.setPassword(passwordEncoder.encode(nueva));
        usuario.setVersionToken(usuario.getVersionToken() + 1); // invalida todos los JWT emitidos antes
        usuarioRepository.save(usuario);

        // Cualquier otro enlace de recuperación pendiente deja de servir
        LocalDateTime ahora = LocalDateTime.now();
        List<PasswordResetTokenModel> pendientes = tokenRepository.findByUsuarioIdAndUsadoEnIsNull(usuario.getId());
        pendientes.forEach(t -> t.setUsadoEn(ahora));
        tokenRepository.saveAll(pendientes);
    }

    private Optional<PasswordResetTokenModel> buscarVigente(String tokenPlano) {
        if (tokenPlano == null || tokenPlano.isBlank()) return Optional.empty();
        return tokenRepository.findByTokenHash(hash(tokenPlano.trim()))
                .filter(t -> t.getUsadoEn() == null && t.getExpiraEn().isAfter(LocalDateTime.now()));
    }

    private String generarTokenPlano() {
        byte[] bytes = new byte[32]; // 256 bits de aleatoriedad
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String valor) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}