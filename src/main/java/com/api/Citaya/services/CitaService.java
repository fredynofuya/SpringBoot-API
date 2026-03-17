package com.api.Citaya.services;

import com.api.Citaya.models.CitaModel;
import com.api.Citaya.repositories.ICitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        citaModel.setFecha(request.getFecha() != null ? request.getFecha() : citaModel.getFecha());
        citaModel.setHora(request.getHora() != null ? request.getHora() : citaModel.getHora());
        citaModel.setEstado(request.getEstado() != null ? request.getEstado() : citaModel.getEstado());
        citaModel.setMensaje(request.getMensaje() != null ? request.getMensaje() : citaModel.getMensaje());

        return citaRepository.save(citaModel);
    }

}
