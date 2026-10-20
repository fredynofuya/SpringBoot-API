package com.api.Citaya.services;

import com.api.Citaya.dto.MedicoEdicionDTO;
import com.api.Citaya.dto.MedicoRegistroDTO;
import com.api.Citaya.models.MedicoModel;
import com.api.Citaya.models.UsuarioModel;
import com.api.Citaya.repositories.IConsultorioRepository;
import com.api.Citaya.repositories.IEspecialidadRepository;
import com.api.Citaya.repositories.IMedicoRepository;
import com.api.Citaya.repositories.IUsuarioRepository;
import com.api.Citaya.security.PoliticaPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

//import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class MedicoService {

    // Límites alineados con las columnas de la BD (usuarios.nombre, usuarios.email, medicos.registro_profesional)
    private static final int MAX_NOMBRE = 100;
    private static final int MAX_EMAIL = 120;
    private static final int MAX_REGISTRO = 50;

    // Política de contraseña: mismo mínimo que la sección Usuarios. BCrypt solo procesa 72 bytes.
    //private static final int MIN_PASSWORD = 8;
    //private static final int MAX_PASSWORD_BYTES = 72;

    private static final String EMAIL_REGEX = "^\\S+@\\S+\\.\\S+$";

    @Autowired
    private IMedicoRepository medicoRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IEspecialidadRepository especialidadRepository;

    @Autowired
    private IConsultorioRepository consultorioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ArrayList<MedicoModel> getMedicos() {
        return (ArrayList<MedicoModel>) medicoRepository.findAll();
    }

    public Optional<MedicoModel> getById(int id) {
        return medicoRepository.findById(id);
    }

    // Crea el usuario (rol MEDICO, contraseña hasheada) y el perfil de médico en una
    // sola transacción: si algo falla, no queda un usuario sin perfil.
    @Transactional
    public MedicoModel crearMedicoConUsuario(MedicoRegistroDTO dto) {
        String nombre = textoObligatorio(dto.getNombre(), "El nombre", MAX_NOMBRE);
        String email = emailValido(dto.getEmail());
        String registro = textoObligatorio(dto.getRegistro_profesional(), "El registro profesional", MAX_REGISTRO);
        PoliticaPassword.validar(dto.getPassword());
//        validarPassword(dto.getPassword());

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }
        if (dto.getId_especialidad() == null || !especialidadRepository.existsById(dto.getId_especialidad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La especialidad seleccionada no existe");
        }
        if (dto.getId_consultorio() == null || !consultorioRepository.existsById(dto.getId_consultorio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El consultorio seleccionado no existe");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(UsuarioModel.Rol.MEDICO);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        MedicoModel medico = new MedicoModel();
        medico.setId_usuario(usuario.getId());
        medico.setId_especialidad(dto.getId_especialidad());
        medico.setId_consultorio(dto.getId_consultorio());
        medico.setRegistro_profesional(registro);

        return medicoRepository.save(medico);
    }

    // Los campos son opcionales (null = no cambia), pero los que lleguen deben ser válidos.
    @Transactional
    public MedicoModel editarMedicoConUsuario(int id, MedicoEdicionDTO dto) {
        MedicoModel medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico no encontrado"));

        UsuarioModel usuario = usuarioRepository.findById(medico.getId_usuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario asociado no encontrado"));

        // 1) Validar todo antes de modificar nada
        String nombre = dto.getNombre() != null
                ? textoObligatorio(dto.getNombre(), "El nombre", MAX_NOMBRE) : null;
        String email = dto.getEmail() != null
                ? emailValido(dto.getEmail()) : null;
        String registro = dto.getRegistro_profesional() != null
                ? textoObligatorio(dto.getRegistro_profesional(), "El registro profesional", MAX_REGISTRO) : null;

        if (email != null && !email.equalsIgnoreCase(usuario.getEmail())) {
            Optional<UsuarioModel> existente = usuarioRepository.findByEmail(email);
            if (existente.isPresent() && existente.get().getId() != usuario.getId()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro usuario con ese correo");
            }
        }
        if (dto.getId_especialidad() != null && !especialidadRepository.existsById(dto.getId_especialidad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La especialidad seleccionada no existe");
        }
        if (dto.getId_consultorio() != null && !consultorioRepository.existsById(dto.getId_consultorio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El consultorio seleccionado no existe");
        }

        // 2) Aplicar los cambios
        if (email != null) usuario.setEmail(email);
        if (nombre != null) usuario.setNombre(nombre);
        usuarioRepository.save(usuario);

        if (dto.getId_especialidad() != null) medico.setId_especialidad(dto.getId_especialidad());
        if (dto.getId_consultorio() != null) medico.setId_consultorio(dto.getId_consultorio());
        if (registro != null) medico.setRegistro_profesional(registro);

        return medicoRepository.save(medico);
    }

    // Reemplaza el DELETE físico: activa/desactiva la cuenta asociada.
    @Transactional
    public MedicoModel cambiarEstadoMedico(int id, boolean activo) {
        MedicoModel medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico no encontrado"));

        UsuarioModel usuario = usuarioRepository.findById(medico.getId_usuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario asociado no encontrado"));

        usuario.setActivo(activo);
        usuarioRepository.save(usuario);

        return medico;
    }

    /* ---------- Validaciones ---------- */

    private String textoObligatorio(String valor, String etiqueta, int max) {
        if (valor == null || valor.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, etiqueta + " es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > max) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    etiqueta + " no puede superar " + max + " caracteres");
        }
        return limpio;
    }

    private String emailValido(String email) {
        String limpio = textoObligatorio(email, "El correo", MAX_EMAIL);
        if (!limpio.matches(EMAIL_REGEX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correo inválido");
        }
        return limpio;
    }

//    private void validarPassword(String password) {
//        if (password == null || password.length() < MIN_PASSWORD) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                    "La contraseña debe tener al menos " + MIN_PASSWORD + " caracteres");
//        }
//        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es demasiado larga");
//        }
//    }
}