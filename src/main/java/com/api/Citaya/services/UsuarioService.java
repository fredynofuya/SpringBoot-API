package com.api.Citaya.services;

import com.api.Citaya.dto.*;
import com.api.Citaya.models.UsuarioModel;
import com.api.Citaya.repositories.IUsuarioRepository;
import com.api.Citaya.security.SesionActual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    // Los médicos se gestionan desde su propia sección, no desde aquí.
    private static final List<UsuarioModel.Rol> ROLES_ADMINISTRATIVOS =
            List.of(UsuarioModel.Rol.SUPER_ADMIN, UsuarioModel.Rol.ADMIN);

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SesionActual sesion;

    public List<UsuarioResumenDTO> listar() {
        return usuarioRepository.findByRolIn(ROLES_ADMINISTRATIVOS).stream()
                .map(this::aResumen)
                .toList();
    }

    public UsuarioResumenDTO crear(UsuarioRegistroDTO dto) {
        validarNombreYEmail(dto.getNombre(), dto.getEmail());
        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres");
        }
        if (usuarioRepository.findByEmail(dto.getEmail().trim()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNombre(dto.getNombre().trim());
        usuario.setEmail(dto.getEmail().trim());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(UsuarioModel.Rol.ADMIN); // por esta vía solo se crean administradores
        usuario.setActivo(true);

        return aResumen(usuarioRepository.save(usuario));
    }

    public UsuarioResumenDTO editar(int id, UsuarioEdicionDTO dto) {
        UsuarioModel usuario = obtenerAdministrativo(id);
        validarNombreYEmail(dto.getNombre(), dto.getEmail());

        String email = dto.getEmail().trim();
        if (!email.equalsIgnoreCase(usuario.getEmail())) {
            usuarioRepository.findByEmail(email).ifPresent(existente -> {
                if (existente.getId() != id) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro usuario con ese correo");
                }
            });
        }

        usuario.setNombre(dto.getNombre().trim());
        usuario.setEmail(email);
        return aResumen(usuarioRepository.save(usuario));
    }

    public UsuarioResumenDTO cambiarEstado(int id, boolean activo) {
        UsuarioModel usuario = obtenerAdministrativo(id);

        boolean esElMismo = sesion.idUsuarioActual().map(actual -> actual == id).orElse(false);
        if (esElMismo && !activo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes desactivar tu propia cuenta");
        }

        usuario.setActivo(activo);
        return aResumen(usuarioRepository.save(usuario));
    }

    private UsuarioModel obtenerAdministrativo(int id) {
        return usuarioRepository.findById(id)
                .filter(u -> ROLES_ADMINISTRATIVOS.contains(u.getRol()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private void validarNombreYEmail(String nombre, String email) {
        if (nombre == null || nombre.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        if (email == null || !email.trim().matches("^\\S+@\\S+\\.\\S+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correo inválido");
        }
    }

    private UsuarioResumenDTO aResumen(UsuarioModel u) {
        return new UsuarioResumenDTO(u.getId(), u.getNombre(), u.getEmail(),
                u.getRol().name(), Boolean.TRUE.equals(u.getActivo()), u.getFecha_creacion());
    }
}