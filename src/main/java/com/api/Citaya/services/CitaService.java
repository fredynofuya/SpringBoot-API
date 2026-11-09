package com.api.Citaya.services;

import com.api.Citaya.models.CitaModel;
import com.api.Citaya.models.PacienteModel;
import com.api.Citaya.repositories.ICitaRepository;
import com.api.Citaya.repositories.IPacienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class CitaService {

    @Autowired
    private ICitaRepository citaRepository;

    @Autowired
    private IPacienteRepository pacienteRepository;

    // =========================================================
    // CONSULTAR CITAS
    // =========================================================

    // Obtener únicamente las citas activas.
    public ArrayList<CitaModel> getCita() {
        return new ArrayList<>(citaRepository.findByActivoTrue());
    }

    // Obtener una cita por ID, siempre que esté activa.
    public Optional<CitaModel> getById(int id) {
        return citaRepository.findByIdAndActivoTrue(id);
    }

    // Obtener citas activas por estado.
    public List<CitaModel> getByEstado(String estado) {

        CitaModel.Estado estadoEnum = CitaModel.Estado.valueOf(
                estado.trim().toUpperCase(Locale.ROOT)
        );

        return citaRepository.findByActivoTrue()
                .stream()
                .filter(c -> c.getEstado() == estadoEnum)
                .toList();
    }

    // Obtener citas activas de un médico por estado.
    public List<CitaModel> getByMedicoYEstado(
            Integer idMedico,
            String estado) {

        CitaModel.Estado estadoEnum = CitaModel.Estado.valueOf(
                estado.trim().toUpperCase(Locale.ROOT)
        );

        return citaRepository.findByActivoTrue()
                .stream()
                .filter(c -> Objects.equals(c.getId_medico(), idMedico))
                .filter(c -> c.getEstado() == estadoEnum)
                .toList();
    }

    // =========================================================
    // CONSULTAR HORARIOS OCUPADOS
    // =========================================================

    public List<LocalTime> getHorasOcupadas(
            Integer idMedico,
            LocalDate fecha) {

        return citaRepository.findByIdMedicoAndFecha(idMedico, fecha)
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .filter(c -> c.getEstado() != CitaModel.Estado.CANCELADA)
                .map(CitaModel::getHora)
                .filter(Objects::nonNull)
                .toList();
    }

    // =========================================================
    // VALIDAR CONFLICTOS DE HORARIO
    // =========================================================

    public boolean existeConflictoHorario(
            Integer idMedico,
            LocalDate fecha,
            LocalTime hora,
            int idCitaActual) {

        if (idMedico == null || fecha == null || hora == null) {
            return false;
        }

        return citaRepository.findByIdMedicoAndFecha(idMedico, fecha)
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .anyMatch(c ->
                        c.getId() != idCitaActual
                                && c.getEstado() != CitaModel.Estado.CANCELADA
                                && hora.equals(c.getHora())
                );
    }

    // =========================================================
    // CREAR CITA CON PACIENTE
    // =========================================================

    @Transactional
    public CitaModel postCitaConPaciente(
            PacienteModel paciente,
            CitaModel cita) {

        // El paciente queda activo si es nuevo o estaba inactivo.
        paciente.setActivo(true);

        // Guardar o reactivar el paciente.
        PacienteModel pacienteGuardado =
                pacienteRepository.save(paciente);

        // Asociar la cita al paciente guardado.
        cita.setId_paciente(pacienteGuardado.getId());

        // Toda cita nueva comienza activa.
        cita.setActivo(true);

        // Guardar la cita.
        return citaRepository.save(cita);
    }

    // =========================================================
    // CREAR CITA SIN MODIFICAR PACIENTE
    // =========================================================

    public CitaModel postCita(CitaModel cita) {

        // Toda cita nueva comienza activa.
        cita.setActivo(true);

        return citaRepository.save(cita);
    }

    // =========================================================
    // DESACTIVAR CITAS DE UN PACIENTE
    // =========================================================

    @Transactional
    public int desactivarCitasPorPaciente(Integer idPaciente) {

        if (idPaciente == null) {
            return 0;
        }

        return citaRepository.desactivarCitasPorPaciente(idPaciente);
    }

    // =========================================================
    // DESACTIVAR UNA CITA
    // =========================================================

    @Transactional
    public Boolean deleteCita(int id) {

        Optional<CitaModel> citaOpt =
                citaRepository.findByIdAndActivoTrue(id);

        if (citaOpt.isEmpty()) {
            return false;
        }

        CitaModel cita = citaOpt.get();

        // Se desactiva únicamente la cita.
        // El paciente asociado no se modifica.
        cita.setActivo(false);
        cita.setFecha_actualizacion(LocalDateTime.now());

        citaRepository.save(cita);

        return true;
    }

    // =========================================================
    // ACTUALIZAR CITA
    // =========================================================

    @Transactional
    public CitaModel putCita(CitaModel request, int id) {

        CitaModel citaModel = citaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() ->
                        new RuntimeException("Cita no encontrada o inactiva")
                );

        // Actualizar fecha.
        citaModel.setFecha(
                request.getFecha() != null
                        ? request.getFecha()
                        : citaModel.getFecha()
        );

        // Actualizar hora.
        citaModel.setHora(
                request.getHora() != null
                        ? request.getHora()
                        : citaModel.getHora()
        );

        // Actualizar estado.
        citaModel.setEstado(
                request.getEstado() != null
                        ? request.getEstado()
                        : citaModel.getEstado()
        );

        // Actualizar médico.
        citaModel.setId_medico(
                request.getId_medico() != null
                        ? request.getId_medico()
                        : citaModel.getId_medico()
        );

        // Actualizar motivo.
        citaModel.setMotivo(
                request.getMotivo() != null
                        ? request.getMotivo()
                        : citaModel.getMotivo()
        );

        // Actualizar observaciones.
        citaModel.setObservaciones(
                request.getObservaciones() != null
                        ? request.getObservaciones()
                        : citaModel.getObservaciones()
        );

        // Actualizar datos del paciente almacenados en la cita.
        citaModel.setNombre(
                request.getNombre() != null
                        ? request.getNombre()
                        : citaModel.getNombre()
        );

        citaModel.setDocumento(
                request.getDocumento() != null
                        ? request.getDocumento()
                        : citaModel.getDocumento()
        );

        citaModel.setEmail(
                request.getEmail() != null
                        ? request.getEmail()
                        : citaModel.getEmail()
        );

        citaModel.setTelefono(
                request.getTelefono() != null
                        ? request.getTelefono()
                        : citaModel.getTelefono()
        );

        citaModel.setEps(
                request.getEps() != null
                        ? request.getEps()
                        : citaModel.getEps()
        );

        // Actualizar fecha de modificación.
        citaModel.setFecha_actualizacion(LocalDateTime.now());

        return citaRepository.save(citaModel);
    }
}