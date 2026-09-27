package com.api.Citaya.services;

import com.api.Citaya.models.CitaModel;
import com.api.Citaya.repositories.ICitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CitaService {

    @Autowired
    private ICitaRepository citaRepository;

    public ArrayList <CitaModel> getCita() {
        return (ArrayList<CitaModel>) citaRepository.findAll();
    }

    public Optional <CitaModel> getById(int id) {
        return citaRepository.findById(id);
    }

    public List<CitaModel> getByEstado(String estado) {

        CitaModel.Estado estadoEnum =
                CitaModel.Estado.valueOf(estado.toUpperCase());

        return citaRepository.findByEstado(estadoEnum);
    }

    // Agregar dentro de CitaService, junto a getByEstado
    public List<CitaModel> getByMedicoYEstado(Integer idMedico, String estado) {
        CitaModel.Estado estadoEnum = CitaModel.Estado.valueOf(estado.toUpperCase());
        return citaRepository.findByIdMedicoAndEstado(idMedico, estadoEnum);
    }


    // Devuelve las horas ya ocupadas de un médico en una fecha, ignorando citas CANCELADA
    public List<LocalTime> getHorasOcupadas(Integer idMedico, LocalDate fecha) {
        return citaRepository.findByIdMedicoAndFecha(idMedico, fecha).stream()
                .filter(c -> c.getEstado() != CitaModel.Estado.CANCELADA)
                .map(CitaModel::getHora)
                .filter(Objects::nonNull)
                .toList();
    }

    // Valida si asignar (idMedico, fecha, hora) choca con otra cita existente (distinta de idCitaActual)
    public boolean existeConflictoHorario(Integer idMedico, LocalDate fecha, LocalTime hora, int idCitaActual) {
        if (idMedico == null || fecha == null || hora == null) return false;

        return citaRepository.findByIdMedicoAndFecha(idMedico, fecha).stream()
                .anyMatch(c -> c.getId() != idCitaActual
                        && c.getEstado() != CitaModel.Estado.CANCELADA
                        && hora.equals(c.getHora()));
    }

    public CitaModel postCita(CitaModel cita) {
        return citaRepository.save(cita);
    }

    public Boolean deleteCita(int id) {
        try {
            citaRepository.deleteById(id);
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    public CitaModel putCita(CitaModel request, int id) {
        CitaModel citaModel = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        // Actualizamos los campos de la cita, si el campo del request es null, se mantiene el valor original de la cita
        citaModel.setFecha(request.getFecha() != null ? request.getFecha() : citaModel.getFecha());
        citaModel.setHora(request.getHora() != null ? request.getHora() : citaModel.getHora());
        citaModel.setEstado(request.getEstado() != null ? request.getEstado() : citaModel.getEstado());
        citaModel.setId_medico(request.getId_medico() != null ? request.getId_medico() : citaModel.getId_medico());
        citaModel.setMensaje(request.getMensaje() != null ? request.getMensaje() : citaModel.getMensaje());
        citaModel.setObservaciones(request.getObservaciones() != null ? request.getObservaciones() : citaModel.getObservaciones());
        // Los campos relacionados con el paciente se actualizan con los datos del request, si el campo del request es null,
        // se mantiene el valor original de la cita
        citaModel.setNombre(request.getNombre() != null ? request.getNombre() : citaModel.getNombre());
        citaModel.setDocumento(request.getDocumento() != null ? request.getDocumento() : citaModel.getDocumento());
        citaModel.setEmail(request.getEmail() != null ? request.getEmail() : citaModel.getEmail());
        citaModel.setTelefono(request.getTelefono() != null ? request.getTelefono() : citaModel.getTelefono());
        citaModel.setEps(request.getEps() != null ? request.getEps() : citaModel.getEps());

        return citaRepository.save(citaModel);
    }

}
