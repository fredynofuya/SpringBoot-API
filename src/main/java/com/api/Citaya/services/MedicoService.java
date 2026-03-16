package com.api.Citaya.services;

import com.api.Citaya.models.MedicoModel;
import com.api.Citaya.repositories.IMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
// Servicio de médicos, contiene la lógica de negocio para manejar los médicos registrados en la base de datos, se encarga de interactuar con el repositorio de médicos para realizar las operaciones CRUD
@Service
public class MedicoService {

    @Autowired
    // Inyección de dependencias del repositorio de médicos
    IMedicoRepository medicoRepository;

    // Muestra todos los médicos registrados en la base de datos
    public ArrayList<MedicoModel> getMedicos() {
        return (ArrayList<MedicoModel>) medicoRepository.findAll();
    }

    // Muestra un médico por id, devuelve un Optional<MedicoModel> para manejar el caso en el que no se encuentre el médico
    public Optional<MedicoModel> getById(int id) {
        return medicoRepository.findById(id);
    }

    // Crea un nuevo médico, devuelve el médico creado
    public MedicoModel postMedico(MedicoModel medico) {
        return medicoRepository.save(medico);
    }

    // Elimina un médico por id, devuelve un booleano indicando si se eliminó o no el médico
    public Boolean deleteMedico(int id) {
        try {
            medicoRepository.deleteById(id);
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    // Actualiza un médico por id, devuelve el médico actualizado
    public MedicoModel putMedico(MedicoModel request, int id) {
        // Obtenemos el médico por id
        MedicoModel medicoModel = medicoRepository.findById(id).get();
        // Actualizamos los campos del médico, si el campo del request es null, se mantiene el valor original del médico
        medicoModel.setRegistro_profesional(request.getRegistro_profesional() != null ? request.getRegistro_profesional() : medicoModel.getRegistro_profesional());
        // Guardamos el médico actualizado en la base de datos y lo devolvemos
        return medicoRepository.save(medicoModel);
    }
}