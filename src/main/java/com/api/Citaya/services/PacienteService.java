package com.api.Citaya.services;

import com.api.Citaya.models.PacienteModel;
import com.api.Citaya.repositories.IPacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    IPacienteRepository pacienteRepository;

    public ArrayList <PacienteModel> getPacientes() {
        return (ArrayList<PacienteModel>) pacienteRepository.findAll();
    }

    public Optional<PacienteModel> getById(int id){
        return pacienteRepository.findById(id);
    }

    public PacienteModel postPaciente(PacienteModel paciente) {
        return pacienteRepository.save(paciente);
    }

    public Boolean deletePaciente(int id) {
        try {
            pacienteRepository.deleteById(id);
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    public PacienteModel putPaciente(PacienteModel request, int id) {
        PacienteModel pacienteModel = pacienteRepository.findById(id).get();

        pacienteModel.setNombre(request.getNombre());
        pacienteModel.setEmail(request.getEmail());
        pacienteModel.setTipo_documento(request.getTipo_documento());
        pacienteModel.setDocumento(request.getDocumento());
        pacienteModel.setTelefono(request.getTelefono());
        pacienteModel.setEps(request.getEps());
        pacienteModel.setFecha_registro(new Date());

        return pacienteModel;
    }




}
