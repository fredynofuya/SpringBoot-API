package com.api.Citaya.controllers;

import com.api.Citaya.models.PacienteModel;
import com.api.Citaya.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping ("/paciente")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public ArrayList<PacienteModel> get() {
        return this.pacienteService.getPacientes();
    }

    @GetMapping(path = "/{id}")
    public Optional<PacienteModel> getPacienteById(@PathVariable("id") int id) {
        return this.pacienteService.getById(id);
    }

    @PostMapping
    public PacienteModel post(@RequestBody PacienteModel paciente) {
        return this.pacienteService.postPaciente(paciente);
    }

    @PutMapping(path = "/{id}")
    public PacienteModel put(@RequestBody PacienteModel request, @PathVariable("id") int id) {
        return this.pacienteService.putPaciente(request, id);
    }

    @DeleteMapping(path = "/{id}")
    public String delete(@PathVariable("id") int id) {
        boolean ok= this.pacienteService.deletePaciente(id);
        return ok? "Se eliminó el paciente con id " + id : "No se pudo eliminar el paciente con id " + id;
    }
}
