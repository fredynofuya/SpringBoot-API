package com.api.Citaya.controllers;

import com.api.Citaya.models.CitaModel;
import com.api.Citaya.models.PacienteModel;
import com.api.Citaya.repositories.IPacienteRepository;
import com.api.Citaya.services.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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

        // Extraído a un método reutilizable: antes solo lo usaba get(), ahora lo usan
        // también getByEstado() y getByMedicoYEstado() — así no se vuelve a olvidar
        // enriquecer un endpoint nuevo con los datos del paciente.
        private void enriquecerConDatosPaciente(List<CitaModel> citas) {
            for (CitaModel cita : citas) {
                Optional<PacienteModel> pacienteOpt = pacienteRepository.findById(cita.getId_paciente());

                if (pacienteOpt.isPresent()) {
                    PacienteModel paciente = pacienteOpt.get();
                    cita.setNombre(paciente.getNombre());
                    cita.setEmail(paciente.getEmail());
                    cita.setTelefono(paciente.getTelefono());
                    cita.setDocumento(paciente.getDocumento());
                    cita.setTipo_documento(paciente.getTipo_documento() != null
                            ? paciente.getTipo_documento().name() : null);
                    cita.setEps(paciente.getEps() != null
                            ? paciente.getEps().name() : null);
                }
            }
        }


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

                    cita.setDocumento(paciente.getDocumento());
                    cita.setTipo_documento(paciente.getTipo_documento() !=null
                            ? paciente.getTipo_documento().name(): null);

                    cita.setEps(paciente.getEps() != null
                            ? paciente.getEps().name() : null);
                }
            }

            return citas;
        }

        // Obtener una cita por id
        @GetMapping(path = "/{id}")
        public Optional<CitaModel> getCitaById(@PathVariable("id") int id) {
            return this.citaService.getById(id);
        }

        @GetMapping("/estado/{estado}")
        public List<CitaModel> getByEstado(@PathVariable String estado) {
            List<CitaModel> citas = citaService.getByEstado(estado);
            enriquecerConDatosPaciente(citas);
            return citas;
        }

        // Nuevo: agenda de un médico específico, filtrada por estado (para la Agenda exclusiva)
        @GetMapping("/medico/{idMedico}/estado/{estado}")
        public List<CitaModel> getByMedicoYEstado(@PathVariable Integer idMedico, @PathVariable String estado) {
            List<CitaModel> citas = citaService.getByMedicoYEstado(idMedico, estado);
            enriquecerConDatosPaciente(citas);
            return citas;
        }

    // Horas ya ocupadas de un médico en una fecha, para pintar el grid de disponibilidad
    @GetMapping("/disponibilidad")
    public List<String> getHorasOcupadas(
            @RequestParam Integer idMedico,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return citaService.getHorasOcupadas(idMedico, fecha).stream()
                .map(LocalTime::toString)
                .toList();
    }

    // Convierte el texto de tipo_documento que llega en el request a la enum de PacienteModel.
    // Lanza 400 con mensaje claro si el valor no es CC, TI, CE o PASAPORTE.
        private PacienteModel.TipoDocumento parseTipoDocumento(String valor) {
            if (valor == null) return null;
            try {
                return PacienteModel.TipoDocumento.valueOf(valor.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "tipo_documento inválido. Use uno de: CC, TI, PASAPORTE");
            }
        }

        // Convierte el texto de eps que llega en el request a la enum de PacienteModel.
    // Lanza 400 con mensaje claro si el valor no es SURA, SANITAS o SAVIASALUD.
    private PacienteModel.Eps parseEps(String valor) {
        if (valor == null) return null;
        try {
            return PacienteModel.Eps.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "eps inválida. Use uno de: SURA, SANITAS, SAVIASALUD");
        }
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

            PacienteModel.TipoDocumento tipoDoc = parseTipoDocumento(cita.getTipo_documento());
            if (tipoDoc != null) {
                paciente.setTipo_documento(tipoDoc);
            } else if (paciente.getTipo_documento() == null) {
                // Es NOT NULL en la BD: si es paciente nuevo y no llegó el dato, falla explícito en vez de dejar que MySQL lo haga
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipo_documento es obligatorio");
            }

            // eps es NOT NULL en la BD: validar y asignar
            PacienteModel.Eps eps = parseEps(cita.getEps());
            if (eps != null) {
                paciente.setEps(eps);
            } else if (paciente.getEps() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eps es obligatoria");
            }



            // Guardar paciente
            paciente = pacienteRepository.save(paciente);

            // Asignar a cita
            cita.setId_paciente(paciente.getId());
            cita.setNombre(paciente.getNombre());
            cita.setEmail(paciente.getEmail());
            cita.setTelefono(paciente.getTelefono());
            cita.setTipo_documento(paciente.getTipo_documento() != null
                    ? paciente.getTipo_documento().name() : null);
            cita.setEps(paciente.getEps() != null
                    ? paciente.getEps().name() : null);

            return citaService.postCita(cita);
        }

        // Actualizar una cita por id, devuelve la cita actualizada
        @PutMapping(path = "/{id}")
        public CitaModel put(@RequestBody CitaModel request, @PathVariable("id") int id) {

            // Validación de conflicto de horario (sin cambios)
            if (request.getId_medico() != null && request.getFecha() != null && request.getHora() != null) {
                boolean conflicto = citaService.existeConflictoHorario(
                        request.getId_medico(), request.getFecha(), request.getHora(), id);
                if (conflicto) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "El médico ya tiene una cita asignada en esa fecha y hora");
                }
            }

            // Solo se toca el paciente si el request realmente trae su documento.
            // El modal de asignación de médico/fecha/hora (Dashboard) NO manda documento,
            // así que este bloque se salta y solo se actualiza la cita.
            if (request.getDocumento() != null && !request.getDocumento().isBlank()) {

                Optional<PacienteModel> optionalPaciente = pacienteRepository.findByDocumento(request.getDocumento());
                PacienteModel paciente;

                if (optionalPaciente.isPresent()) {
                    paciente = optionalPaciente.get();
                } else {
                    paciente = new PacienteModel();
                    paciente.setDocumento(request.getDocumento());
                }

                var nombre = Optional.ofNullable(request.getNombre()).orElse(paciente.getNombre());
                var email = Optional.ofNullable(request.getEmail()).orElse(paciente.getEmail());
                var telefono = Optional.ofNullable(request.getTelefono()).orElse(paciente.getTelefono());

                paciente.setNombre(nombre);
                paciente.setEmail(email);
                paciente.setTelefono(telefono);

                PacienteModel.TipoDocumento tipoDoc = parseTipoDocumento(request.getTipo_documento());
                if (tipoDoc != null) {
                    paciente.setTipo_documento(tipoDoc);
                }

                PacienteModel.Eps eps = parseEps(request.getEps());
                if (eps != null) {
                    paciente.setEps(eps);
                }

                paciente = pacienteRepository.save(paciente);

                request.setId_paciente(paciente.getId());
                request.setNombre(paciente.getNombre());
                request.setEmail(paciente.getEmail());
                request.setTelefono(paciente.getTelefono());
                request.setTipo_documento(paciente.getTipo_documento() != null
                        ? paciente.getTipo_documento().name() : null);
                request.setEps(paciente.getEps() != null
                        ? paciente.getEps().name() : null);
            }

            return this.citaService.putCita(request, id);
        }

        // Eliminar una cita por id, devuelve un mensaje indicando si se eliminó o no la cita
        @DeleteMapping(path = "/{id}")
        public String delete(@PathVariable("id") int id) {
            boolean ok= this.citaService.deleteCita(id);
            return ok? "Se eliminó la cita con id " + id : "No se pudo eliminar la cita con id " + id;
        }
}
