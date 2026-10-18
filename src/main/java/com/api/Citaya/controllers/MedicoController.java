package com.api.Citaya.controllers;

import com.api.Citaya.dto.MedicoEdicionDTO;
import com.api.Citaya.dto.MedicoEstadoDTO;
import com.api.Citaya.dto.MedicoRegistroDTO;
import com.api.Citaya.models.MedicoModel;
import com.api.Citaya.repositories.IConsultorioRepository;
import com.api.Citaya.repositories.IUsuarioRepository;
import com.api.Citaya.security.SoloAdministracion;
import com.api.Citaya.services.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/medico")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IConsultorioRepository consultorioRepository;

    @GetMapping
    @SoloAdministracion
    public ArrayList<MedicoModel> get() {
        ArrayList<MedicoModel> medicos = this.medicoService.getMedicos();

        for (MedicoModel medico : medicos) {
            usuarioRepository.findById(medico.getId_usuario()).ifPresent(u -> {
                medico.setNombreMedico(u.getNombre());
                medico.setEmailMedico(u.getEmail());
                medico.setActivoMedico(u.getActivo());
            });

            consultorioRepository.findById(medico.getId_consultorio()).ifPresent(c -> {
                medico.setNombreConsultorio(c.getNombre());
                medico.setUbicacionConsultorio(c.getUbicacion());
            });
        }

        return medicos;
    }

    @GetMapping(path = "/{id}")
    @SoloAdministracion
    public Optional<MedicoModel> getMedicoById(@PathVariable("id") int id) {

        return this.medicoService.getById(id);
    }

    @PostMapping
    @SoloAdministracion
    public MedicoModel post(@RequestBody MedicoRegistroDTO dto) {
        return this.medicoService.crearMedicoConUsuario(dto);
    }

    @PutMapping(path = "/{id}")
    @SoloAdministracion
    public MedicoModel put(@RequestBody MedicoEdicionDTO dto, @PathVariable("id") int id) {
        return this.medicoService.editarMedicoConUsuario(id, dto);
    }

    // Reemplaza el antiguo @DeleteMapping: ya no se borra el médico, se activa/desactiva.
    @PatchMapping(path = "/{id}/estado")
    @SoloAdministracion
    public MedicoModel cambiarEstado(@RequestBody MedicoEstadoDTO dto, @PathVariable("id") int id) {
        return this.medicoService.cambiarEstadoMedico(id, dto.isActivo());
    }
}
