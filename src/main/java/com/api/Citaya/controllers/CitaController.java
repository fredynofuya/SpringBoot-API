package com.api.Citaya.controllers;

import com.api.Citaya.models.CitaModel;
import com.api.Citaya.models.PacienteModel;
import com.api.Citaya.repositories.IPacienteRepository;
import com.api.Citaya.services.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

// Permite solicitudes de cualquier origen, lo que es útil para el desarrollo y pruebas, pero se debe configurar
// adecuadamente en producción para evitar problemas de seguridad
@CrossOrigin("*")
// Controlador REST para manejar las solicitudes relacionadas con las citas, mapea las solicitudes a "/cita"
@RestController
// Mapea las solicitudes a "/cita"
@RequestMapping ("/cita")
public class CitaController {
        // Inyección de dependencia del servicio de cita para manejar la lógica de negocio relacionada con las citas
        @Autowired
        private CitaService citaService;
        // Inyección de dependencia del repositorio de pacientes para manejar la lógica de negocio relacionada con los
        // pacientes, se encarga de interactuar con el repositorio de pacientes para realizar las operaciones CRUD
        // relacionadas con los pacientes, ya que al crear una cita, se debe verificar si el paciente existe o no
        // en la base de datos de pacientes, y si no existe, se debe crear un nuevo paciente con los datos de la cita,
        // y si existe, se deben actualizar los datos del paciente con los datos de la cita, para que se mantengan a
        // ctualizados los datos del paciente en la base de datos de pacientes, ya que se asume que el paciente se
        // actualiza en el momento de la creación de la cita
        @Autowired
        private IPacienteRepository pacienteRepository;

        // obtener todas las citas, devuelve una lista de citas
//        @GetMapping
//        public ArrayList<CitaModel> get() {
//            return this.citaService.getCita();
//        }
        @GetMapping
        public ArrayList<CitaModel> get() {

            ArrayList<CitaModel> citas = citaService.getCita();

            for (CitaModel cita : citas) {

                Optional<PacienteModel> pacienteOpt =
                        pacienteRepository.findById(cita.getId_paciente());

                if (pacienteOpt.isPresent()) {
                    PacienteModel paciente = pacienteOpt.get();

                    cita.setNombre(paciente.getNombre());
                    cita.setEmail(paciente.getEmail());
                    cita.setTelefono(paciente.getTelefono());
                    cita.setEps(paciente.getEps());
                    cita.setDocumento(paciente.getDocumento());
                    //cita.setTipo_documento(paciente.getTipoDocumento());
                    //cita.setFecha_registro(paciente.getFechaRegistro());
                }
            }

            return citas;
        }

        // Obtener una cita por id
        @GetMapping(path = "/{id}")
        public Optional<CitaModel> getCitaById(@PathVariable("id") int id) {
            return this.citaService.getById(id);
        }

        // Crear una nueva cita, devuelve la cita creada, si el paciente no existe, se crea un nuevo paciente con los datos
        // de la cita, si el paciente existe, se actualizan los datos del paciente con los datos de la cita, para que se
        // mantengan actualizados los datos del paciente en la base de datos de pacientes, ya que se asume que el paciente
        // se actualiza en el momento de la creación de la cita
        @PostMapping
        public CitaModel post(@RequestBody CitaModel cita) {
            // Obtener el paciente por documento, si el paciente no existe, se crea un nuevo paciente con los datos de la cita,
            // si el paciente existe,
            // se actualizan los datos del paciente con los datos de la cita
            String documento = String.valueOf(cita.getDocumento());

            Optional<PacienteModel> optionalPaciente = pacienteRepository.findByDocumento(documento);

            PacienteModel paciente;

            if (optionalPaciente.isPresent()) {
                    paciente = optionalPaciente.get();
            } else {
                    paciente = new PacienteModel();
                    paciente.setDocumento(documento);
            }

            // Actualizar datos
            paciente.setNombre(cita.getNombre());
            paciente.setEmail(cita.getEmail());
            paciente.setTelefono(cita.getTelefono());
            paciente.setEps(cita.getEps());

            // Guardar paciente
            paciente = pacienteRepository.save(paciente);

            // Asignar a cita
            cita.setId_paciente(paciente.getId());
            cita.setNombre(paciente.getNombre());
            cita.setEmail(paciente.getEmail());
            cita.setTelefono(paciente.getTelefono());
            cita.setEps(paciente.getEps());

            return citaService.postCita(cita);
        }

        // Actualizar una cita por id, devuelve la cita actualizada
        @PutMapping(path = "/{id}")
        public CitaModel put(@RequestBody CitaModel request, @PathVariable("id") int id) {

            String documento = String.valueOf(request.getDocumento());
            Optional <PacienteModel> optionalPaciente = pacienteRepository.findByDocumento(documento);
            PacienteModel paciente;

            if (optionalPaciente.isPresent()) {
                    paciente = optionalPaciente.get();
            } else {
                    paciente = new PacienteModel();
                    paciente.setDocumento(documento);
            }

            var nombre = Optional.ofNullable(request.getNombre())
                    .orElse(paciente.getNombre());

            var email = Optional.ofNullable(request.getEmail())
                    .orElse(paciente.getEmail());

            var telefono = Optional.ofNullable(request.getTelefono())
                    .orElse(paciente.getTelefono());

            var eps = Optional.ofNullable(request.getEps())
                    .orElse(paciente.getEps());

            // Actualizar datos
            paciente.setNombre(nombre);
            paciente.setEmail(email);
            paciente.setTelefono(telefono);
            paciente.setEps(eps);
            // Guardar paciente
            paciente = pacienteRepository.save(paciente);
            // Asignar a cita
            request.setId_paciente(paciente.getId());
            request.setNombre(paciente.getNombre());
            request.setEmail(paciente.getEmail());
            request.setTelefono(paciente.getTelefono());
            request.setEps(paciente.getEps());

            return this.citaService.putCita(request, id);
        }

        // Eliminar una cita por id, devuelve un mensaje indicando si se eliminó o no la cita
        @DeleteMapping(path = "/{id}")
        public String delete(@PathVariable("id") int id) {
            boolean ok= this.citaService.deleteCita(id);
            return ok? "Se eliminó la cita con id " + id : "No se pudo eliminar la cita con id " + id;
        }
}
