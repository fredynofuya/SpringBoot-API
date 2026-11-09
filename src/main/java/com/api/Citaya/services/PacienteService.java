package com.api.Citaya.services;

import com.api.Citaya.models.PacienteModel;
import com.api.Citaya.repositories.IPacienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private IPacienteRepository pacienteRepository;

    @Autowired
    private CitaService citaService;

    // =========================================================
    // CONSULTAR PACIENTES
    // =========================================================

    // Obtener únicamente los pacientes activos.
    public List<PacienteModel> getPacientes() {
        return pacienteRepository.findByActivoTrue();
    }

    // Obtener un paciente por ID, siempre que esté activo.
    public Optional<PacienteModel> getById(int id) {
        return pacienteRepository.findByIdAndActivoTrue(id);
    }

    // Buscar paciente por documento.
    // Incluye pacientes inactivos para evitar registros duplicados.
    public Optional<PacienteModel> getByDocumento(String documento) {
        return pacienteRepository.findByDocumento(documento);
    }

    // =========================================================
    // CREAR PACIENTE
    // =========================================================

    public PacienteModel postPaciente(PacienteModel paciente) {

        boolean acepta = Boolean.TRUE.equals(
                paciente.getAcepta_notificaciones()
        );

        paciente.setAcepta_notificaciones(acepta);

        paciente.setFecha_consentimiento(
                acepta ? LocalDateTime.now() : null
        );

        // Todo paciente nuevo comienza activo.
        paciente.setActivo(true);

        return pacienteRepository.save(paciente);
    }

    // =========================================================
    // DESACTIVAR PACIENTE Y SUS CITAS
    // =========================================================

    @Transactional
    public Boolean deletePaciente(int id) {

        Optional<PacienteModel> pacienteOpt =
                pacienteRepository.findById(id);

        if (pacienteOpt.isEmpty()) {
            return false;
        }

        PacienteModel paciente = pacienteOpt.get();

        // Desactivar paciente si todavía está activo.
        if (!Boolean.FALSE.equals(paciente.getActivo())) {

            paciente.setActivo(false);
            paciente.setFecha_actualizacion(LocalDateTime.now());

            pacienteRepository.save(paciente);
        }

        // Desactivar todas las citas activas asociadas.
        // Ambas operaciones pertenecen a la misma transacción.
        citaService.desactivarCitasPorPaciente(id);

        return true;
    }

    // =========================================================
    // ACTUALIZAR PACIENTE
    // =========================================================

    @Transactional
    public PacienteModel putPaciente(
            PacienteModel request,
            int id) {

        PacienteModel pacienteModel = pacienteRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Paciente no encontrado o inactivo"
                        )
                );

        pacienteModel.setNombre(
                request.getNombre() != null
                        ? request.getNombre()
                        : pacienteModel.getNombre()
        );

        pacienteModel.setEmail(
                request.getEmail() != null
                        ? request.getEmail()
                        : pacienteModel.getEmail()
        );

        pacienteModel.setTipo_documento(
                request.getTipo_documento() != null
                        ? request.getTipo_documento()
                        : pacienteModel.getTipo_documento()
        );

        pacienteModel.setDocumento(
                request.getDocumento() != null
                        ? request.getDocumento()
                        : pacienteModel.getDocumento()
        );

        pacienteModel.setTelefono(
                request.getTelefono() != null
                        ? request.getTelefono()
                        : pacienteModel.getTelefono()
        );

        pacienteModel.setEps(
                request.getEps() != null
                        ? request.getEps()
                        : pacienteModel.getEps()
        );

        Boolean acepta = request.getAcepta_notificaciones();

        if (acepta != null &&
                !acepta.equals(pacienteModel.getAcepta_notificaciones())) {

            pacienteModel.setAcepta_notificaciones(acepta);

            pacienteModel.setFecha_consentimiento(
                    acepta ? LocalDateTime.now() : null
            );
        }

        pacienteModel.setFecha_actualizacion(LocalDateTime.now());

        return pacienteRepository.save(pacienteModel);
    }
}