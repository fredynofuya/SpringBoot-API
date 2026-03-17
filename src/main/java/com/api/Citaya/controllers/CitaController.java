package com.api.Citaya.controllers;

import com.api.Citaya.models.CitaModel;
import com.api.Citaya.services.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping ("/cita")
public class CitaController {

    @Autowired
        private CitaService citaService;

        @GetMapping
        public ArrayList<CitaModel> get() {
            return this.citaService.getCita();
        }

        @GetMapping(path = "/{id}")
        public Optional<CitaModel> getCitaById(@PathVariable("id") int id) {
            return this.citaService.getById(id);
        }

        @PostMapping
        public CitaModel post(@RequestBody CitaModel cita) {
            return this.citaService.postCita(cita);
        }

        @PutMapping(path = "/{id}")
        public CitaModel put(@RequestBody CitaModel request, @PathVariable("id") int id) {
            return this.citaService.putCita(request, id);
        }

        @DeleteMapping(path = "/{id}")
        public String delete(@PathVariable("id") int id) {
            boolean ok= this.citaService.deleteCita(id);
            return ok? "Se eliminó la cita con id " + id : "No se pudo eliminar la cita con id " + id;
        }
}
