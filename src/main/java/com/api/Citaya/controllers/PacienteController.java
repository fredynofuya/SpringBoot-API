package com.api.Citaya.controllers;

import com.api.Citaya.models.PacienteModel;
import com.api.Citaya.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
// Controlador REST para manejar las solicitudes relacionadas con los pacientes, mapea las solicitudes a "/paciente"
@RestController
// Mapea las solicitudes a "/paciente"
@RequestMapping ("/paciente")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    // Obtener todos los pacientes, devuelve una lista de pacientes
    public ArrayList<PacienteModel> get() {
        return this.pacienteService.getPacientes();
    }

    //Obtener un paciente por id
    @GetMapping(path = "/{id}")
    public Optional<PacienteModel> getPacienteById(@PathVariable("id") int id) {
        return this.pacienteService.getById(id);
    }

    //Crear un nuevo paciente, devuelve el paciente creado
    @PostMapping
    public PacienteModel post(@RequestBody PacienteModel paciente) {
        return this.pacienteService.postPaciente(paciente);
    }

    //Actualizar un paciente por id, devuelve el paciente actualizado
    @PutMapping(path = "/{id}")
    public PacienteModel put(@RequestBody PacienteModel request, @PathVariable("id") int id) {
        return this.pacienteService.putPaciente(request, id);
    }

    //Elimnar un paciente por id, devuelve un mensaje indicando si se eliminó o no el paciente
    @DeleteMapping(path = "/{id}")
    public String delete(@PathVariable("id") int id) {
        boolean ok= this.pacienteService.deletePaciente(id);
        return ok? "Se eliminó el paciente con id " + id : "No se pudo eliminar el paciente con id " + id;
    }
}
