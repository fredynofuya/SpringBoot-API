package com.api.Citaya.services;

import com.api.Citaya.dto.MedicoEdicionDTO;
import com.api.Citaya.dto.MedicoRegistroDTO;
import com.api.Citaya.models.MedicoModel;
import com.api.Citaya.models.UsuarioModel;
import com.api.Citaya.repositories.IConsultorioRepository;
import com.api.Citaya.repositories.IEspecialidadRepository;
import com.api.Citaya.repositories.IMedicoRepository;
import com.api.Citaya.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MedicoService {

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

    // Crea el usuario (rol MEDICO, contraseña hasheada) y el perfil de médico
    // en una sola operación — así nunca queda un usuario sin perfil o viceversa.
    public MedicoModel crearMedicoConUsuario(MedicoRegistroDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }
        if (dto.getId_especialidad() == null || !especialidadRepository.existsById(dto.getId_especialidad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La especialidad seleccionada no existe");
        }
        if (dto.getId_consultorio() == null || !consultorioRepository.existsById(dto.getId_consultorio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El consultorio seleccionado no existe");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(UsuarioModel.Rol.MEDICO);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        MedicoModel medico = new MedicoModel();
        medico.setId_usuario(usuario.getId());
        medico.setId_especialidad(dto.getId_especialidad());
        medico.setId_consultorio(dto.getId_consultorio());
        medico.setRegistro_profesional(dto.getRegistro_profesional());

        return medicoRepository.save(medico);
    }

    public MedicoModel editarMedicoConUsuario(int id, MedicoEdicionDTO dto) {
        MedicoModel medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico no encontrado"));

        UsuarioModel usuario = usuarioRepository.findById(medico.getId_usuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario asociado no encontrado"));

        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            Optional<UsuarioModel> existente = usuarioRepository.findByEmail(dto.getEmail());
            if (existente.isPresent() && existente.get().getId() != usuario.getId()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro usuario con ese correo");
            }
            usuario.setEmail(dto.getEmail());
        }
        if (dto.getNombre() != null) {
            usuario.setNombre(dto.getNombre());
        }
        usuarioRepository.save(usuario);

        if (dto.getId_especialidad() != null) {
            if (!especialidadRepository.existsById(dto.getId_especialidad())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La especialidad seleccionada no existe");
            }
            medico.setId_especialidad(dto.getId_especialidad());
        }
        if (dto.getId_consultorio() != null) {
            if (!consultorioRepository.existsById(dto.getId_consultorio())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El consultorio seleccionado no existe");
            }
            medico.setId_consultorio(dto.getId_consultorio());
        }
        if (dto.getRegistro_profesional() != null) {
            medico.setRegistro_profesional(dto.getRegistro_profesional());
        }

        return medicoRepository.save(medico);
    }

    // Reemplaza el DELETE físico: activa/desactiva la cuenta asociada.
    public MedicoModel cambiarEstadoMedico(int id, boolean activo) {
        MedicoModel medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico no encontrado"));

        UsuarioModel usuario = usuarioRepository.findById(medico.getId_usuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario asociado no encontrado"));

        usuario.setActivo(activo);
        usuarioRepository.save(usuario);

        return medico;
    }
}

//package com.api.Citaya.services;
//
//import com.api.Citaya.models.MedicoModel;
//import com.api.Citaya.repositories.IMedicoRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Optional;
//// Servicio de médicos, contiene la lógica de negocio para manejar los médicos registrados en la base de datos, se encarga de interactuar con el repositorio de médicos para realizar las operaciones CRUD
//@Service
//public class MedicoService {
//
//    @Autowired
//    // Inyección de dependencias del repositorio de médicos
//    IMedicoRepository medicoRepository;
//
//    // Muestra todos los médicos registrados en la base de datos
//    public ArrayList<MedicoModel> getMedicos() {
//        return (ArrayList<MedicoModel>) medicoRepository.findAll();
//    }
//
//    // Muestra un médico por id, devuelve un Optional<MedicoModel> para manejar el caso en el que no se encuentre el médico
//    public Optional<MedicoModel> getById(int id) {
//        return medicoRepository.findById(id);
//    }
//
//    // Crea un nuevo médico, devuelve el médico creado
//    public MedicoModel postMedico(MedicoModel medico) {
//        return medicoRepository.save(medico);
//    }
//
//    // Elimina un médico por id, devuelve un booleano indicando si se eliminó o no el médico
//    public Boolean deleteMedico(int id) {
//        try {
//            medicoRepository.deleteById(id);
//            return true;
//        } catch (Exception err) {
//            return false;
//        }
//    }
//
//    // Actualiza un médico por id, devuelve el médico actualizado
//    public MedicoModel putMedico(MedicoModel request, int id) {
//        // Obtenemos el médico por id
//        MedicoModel medicoModel = medicoRepository.findById(id).get();
//        // Actualizamos los campos del médico, si el campo del request es null, se mantiene el valor original del médico
//        medicoModel.setRegistro_profesional(request.getRegistro_profesional() != null ? request.getRegistro_profesional() : medicoModel.getRegistro_profesional());
//        // Guardamos el médico actualizado en la base de datos y lo devolvemos
//        return medicoRepository.save(medicoModel);
//    }
//}