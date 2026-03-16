package com.api.Citaya.services;

import com.api.Citaya.models.PacienteModel;
import com.api.Citaya.repositories.IPacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
// Servicio para manejar la lógica de negocio relacionada con los pacientes, se encarga de interactuar con el repositorio de pacientes para realizar las operaciones CRUD
@Service
public class PacienteService {

    @Autowired
    // Inyección de dependencias del repositorio de pacientes
    IPacienteRepository pacienteRepository;

    //Muestra todos los pacientes registrados en la base de datos
    public ArrayList <PacienteModel> getPacientes() {
        return (ArrayList<PacienteModel>) pacienteRepository.findAll();
    }

    //Muestra un paciente por id, devuelve un Optional<PacienteModel> para manejar el caso en el que no se encuentre el paciente
    public Optional<PacienteModel> getById(int id){
        return pacienteRepository.findById(id);
    }

    //Crea un nuevo paciente, devuelve el paciente creado
    public PacienteModel postPaciente(PacienteModel paciente) {
        return pacienteRepository.save(paciente);
    }

    //Elimina un paciente por id, devuelve un booleano indicando si se eliminó o no el paciente
    public Boolean deletePaciente(int id) {
        try {
            pacienteRepository.deleteById(id);
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    //Actualiza un paciente por id, devuelve el paciente actualizado
    public PacienteModel putPaciente(PacienteModel request, int id) {
        //Obtenemos el paciente por id
        PacienteModel pacienteModel = pacienteRepository.findById(id).get();
        //Actualizamos los campos del paciente, si el campo del request es null, se mantiene el valor original del paciente
        pacienteModel.setNombre(request.getNombre() != null ? request.getNombre() : pacienteModel.getNombre());
        pacienteModel.setEmail(request.getEmail() != null ? request.getEmail() : pacienteModel.getEmail());
        pacienteModel.setTipo_documento(request.getTipo_documento() != null ? request.getTipo_documento() : pacienteModel.getTipo_documento());
        pacienteModel.setDocumento(request.getDocumento() != null ? request.getDocumento() : pacienteModel.getDocumento());
        pacienteModel.setTelefono(request.getTelefono() != null ? request.getTelefono() : pacienteModel.getTelefono());
        pacienteModel.setEps(request.getEps() != null ? request.getEps() : pacienteModel.getEps());
        //El campo fecha_registro se actualiza con la fecha actual, ya que se asume que el paciente se actualiza en el momento de la actualización
        pacienteModel.setFecha_registro(new Date());
        //Guardamos el paciente actualizado en la base de datos y lo devolvemos
        return pacienteRepository.save(pacienteModel);
    }
}
