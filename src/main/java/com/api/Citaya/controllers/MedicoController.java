package com.api.Citaya.controllers;

import com.api.Citaya.models.MedicoModel;
import com.api.Citaya.repositories.IConsultorioRepository;
import com.api.Citaya.repositories.IUsuarioRepository;
import com.api.Citaya.services.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

// Controlador REST para manejar las solicitudes relacionadas con los médicos, mapea las solicitudes a "/medico"
// Permite solicitudes de cualquier origen, lo que es útil para el desarrollo y pruebas, pero se debe configurar
// adecuadamente en producción para evitar problemas de seguridad
@CrossOrigin("*")
@RestController
// Mapea las solicitudes a "/medico"
@RequestMapping ("/medico")
public class MedicoController {

    @Autowired
    // Inyección de dependencia del servicio de médico para manejar la lógica de negocio relacionada con los médicos
    private MedicoService medicoService;

    @Autowired
    // Inyección de dependencia del repositorio de usuario para manejar la lógica de negocio relacionada con los usuarios
    private IUsuarioRepository usuarioRepository;

    @Autowired
    // Inyección de dependencia del repositorio de consultorio para manejar la lógica de negocio relacionada con los consultorios
    private IConsultorioRepository consultorioRepository;

    // Obtener todos los médicos, devuelve una lista de médicos
    @GetMapping
    public ArrayList<MedicoModel> get() {
        ArrayList<MedicoModel> medicos = this.medicoService.getMedicos();

        for (MedicoModel medico : medicos) {
            Optional<com.api.Citaya.models.UsuarioModel> usuario =
                    usuarioRepository.findById(medico.getId_usuario());
            usuario.ifPresent(u -> medico.setNombreMedico(u.getNombre()));

            Optional<com.api.Citaya.models.ConsultorioModel> consultorio =
                    consultorioRepository.findById(medico.getId_consultorio());
            consultorio.ifPresent(c -> medico.setNombreConsultorio(c.getNombre()));
        }

        return medicos;
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
