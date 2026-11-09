package com.api.Citaya.controllers;

import com.api.Citaya.models.PacienteModel;
import com.api.Citaya.security.SoloAdministracion;
import com.api.Citaya.services.PacienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/paciente")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // Obtener todos los pacientes activos
    @GetMapping
    public List<PacienteModel> get() {
        return pacienteService.getPacientes();
    }

    // Obtener un paciente activo por ID
    @GetMapping(path = "/{id}")
    public Optional<PacienteModel> getPacienteById(
            @PathVariable("id") int id) {

        return pacienteService.getById(id);
    }

    // Obtener paciente por documento
    @GetMapping("/documento/{documento}")
    public Optional<PacienteModel> getByDocumento(
            @PathVariable String documento) {

        return pacienteService.getByDocumento(documento);
    }

    // Crear paciente
    @PostMapping
    @SoloAdministracion
    public PacienteModel post(@RequestBody PacienteModel paciente) {

        if (paciente.getDocumento() != null &&
                pacienteService.getByDocumento(
                        paciente.getDocumento()
                ).isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un paciente con ese documento"
            );
        }

        return pacienteService.postPaciente(paciente);
    }

    // Actualizar paciente
    @PutMapping(path = "/{id}")
    @SoloAdministracion
    public PacienteModel put(
            @RequestBody PacienteModel request,
            @PathVariable("id") int id) {

        if (request.getDocumento() != null) {

            Optional<PacienteModel> existente =
                    pacienteService.getByDocumento(
                            request.getDocumento()
                    );

            if (existente.isPresent() &&
                    existente.get().getId() != id) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Ya existe otro paciente con ese documento"
                );
            }
        }

        return pacienteService.putPaciente(request, id);
    }

    // Eliminación lógica
    @DeleteMapping(path = "/{id}")
    @SoloAdministracion
    public ResponseEntity<String> delete(
            @PathVariable("id") int id) {

        boolean ok = pacienteService.deletePaciente(id);

        if (ok) {
            return ResponseEntity.ok(
                    "Paciente desactivado correctamente. ID: " + id
            );
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el paciente con ID: " + id);
    }
}
