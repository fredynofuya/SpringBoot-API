package com.api.Citaya.controllers;

import com.api.Citaya.models.MedicoModel;
import com.api.Citaya.services.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
// Controlador REST para manejar las solicitudes relacionadas con los médicos, mapea las solicitudes a "/medico"
@RestController
// Mapea las solicitudes a "/medico"
@RequestMapping ("/medico")
public class MedicoController {

    @Autowired
    // Inyección de dependencia del servicio de médico para manejar la lógica de negocio relacionada con los médicos
    private MedicoService medicoService;

    @GetMapping
    // Obtener todos los médicos, devuelve una lista de médicos
    public ArrayList<MedicoModel> get() {
        return this.medicoService.getMedicos();
    }

    //Obtener un médico por id
    @GetMapping(path = "/{id}")
    // Devuelve un Optional<MedicoModel> para manejar el caso en el que no se encuentre el médico
    Optional <MedicoModel> getMedicoById(@PathVariable("id") int id) {
        return this.medicoService.getById(id);
    }

    // Crear un nuevo médico, devuelve el médico creado
    @PostMapping
    public MedicoModel post(@RequestBody MedicoModel medico) {
        return this.medicoService.postMedico(medico);
    }

    // Actualizar un médico por id, devuelve el médico actualizado
    @PutMapping(path = "/{id}")
    public MedicoModel put(@RequestBody MedicoModel request, @PathVariable("id") int id) {
        return this.medicoService.putMedico(request, id);
    }

    // Eliminar un médico por id, devuelve un mensaje indicando si se eliminó o no el médico
    @DeleteMapping(path = "/{id}")
    public String delete(@PathVariable("id") int id) {
        boolean ok= this.medicoService.deleteMedico(id);
        return ok? "Se eliminó el médico con id " + id : "No se pudo eliminar el médico con id " + id;
    }


}
