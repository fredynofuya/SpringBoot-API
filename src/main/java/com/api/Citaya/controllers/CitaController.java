package com.api.Citaya.controllers;

import com.api.Citaya.models.*;
import com.api.Citaya.repositories.IConsultorioRepository;
import com.api.Citaya.repositories.IMedicoRepository;
import com.api.Citaya.repositories.IPacienteRepository;
import com.api.Citaya.repositories.IUsuarioRepository;
import com.api.Citaya.services.CitaService;
import com.api.Citaya.services.NotificacionService;
import com.api.Citaya.security.SesionActual;
import com.api.Citaya.security.SoloAdministracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/cita")
public class CitaController {

    @Autowired
    private NotificacionService notificaciones;

    @Autowired
    private SesionActual sesion;

    @Autowired
    private IMedicoRepository medicoRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IConsultorioRepository consultorioRepository;

    @Autowired
    private CitaService citaService;

    @Autowired
    private IPacienteRepository pacienteRepository;


    // =========================================================
    // VALIDACIONES DE ACCESO
    // =========================================================

    private void exigirAccesoAMedico(Integer idMedico) {

        if (!sesion.estaAutenticado()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Sesión requerida"
            );
        }

        if (sesion.esAdministracion()) {
            return;
        }

        boolean propio = sesion.esMedico()
                && idMedico != null
                && sesion.idMedicoActual()
                .map(id -> id.equals(idMedico))
                .orElse(false);

        if (!propio) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes acceso a las citas de otro médico"
            );
        }
    }


    // =========================================================
    // ACTUALIZAR ESTADO DE CITA COMO MÉDICO
    // =========================================================

    private CitaModel actualizarEstadoComoMedico(
            CitaModel request,
            int id
    ) {

        CitaModel cita = citaService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada"
                ));

        exigirAccesoAMedico(cita.getId_medico());

        CitaModel.Estado nuevo = request.getEstado();

        boolean permitido =
                (nuevo == CitaModel.Estado.ATENDIDA
                        || nuevo == CitaModel.Estado.CONFIRMADA)
                        &&
                        (cita.getEstado() == CitaModel.Estado.CONFIRMADA
                                || cita.getEstado() == CitaModel.Estado.ATENDIDA);

        if (!permitido) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo puedes marcar tus citas como atendidas o confirmadas"
            );
        }

        // Se ignoran los demás campos que puedan venir en el request.
        CitaModel soloEstado = new CitaModel();
        soloEstado.setEstado(nuevo);

        return citaService.putCita(soloEstado, id);
    }


    // =========================================================
    // COMPLETAR DATOS DEL PACIENTE EN LAS CITAS
    // =========================================================

    private void enriquecerConDatosPaciente(List<CitaModel> citas) {

        for (CitaModel cita : citas) {

            Optional<PacienteModel> pacienteOpt =
                    pacienteRepository.findById(cita.getId_paciente());

            if (pacienteOpt.isPresent()) {

                PacienteModel paciente = pacienteOpt.get();

                cita.setNombre(paciente.getNombre());
                cita.setEmail(paciente.getEmail());
                cita.setTelefono(paciente.getTelefono());
                cita.setDocumento(paciente.getDocumento());

                cita.setTipo_documento(
                        paciente.getTipo_documento() != null
                                ? paciente.getTipo_documento().name()
                                : null
                );

                cita.setEps(
                        paciente.getEps() != null
                                ? paciente.getEps().name()
                                : null
                );

                cita.setFecha_registro(paciente.getFecha_registro());
            }
        }
    }


    // =========================================================
    // COMPLETAR DATOS DEL MÉDICO Y CONSULTORIO
    // =========================================================

    private void enriquecerConDatosMedico(List<CitaModel> citas) {

        Set<Integer> idsMedico = citas.stream()
                .map(CitaModel::getId_medico)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (idsMedico.isEmpty()) {
            return;
        }

        Map<Integer, MedicoModel> medicosPorId =
                medicoRepository.findAllById(idsMedico)
                        .stream()
                        .collect(Collectors.toMap(
                                MedicoModel::getId,
                                m -> m
                        ));

        if (medicosPorId.isEmpty()) {
            return;
        }

        Set<Integer> idsUsuario = medicosPorId.values()
                .stream()
                .map(MedicoModel::getId_usuario)
                .collect(Collectors.toSet());

        Set<Integer> idsConsultorio = medicosPorId.values()
                .stream()
                .map(MedicoModel::getId_consultorio)
                .collect(Collectors.toSet());

        Map<Integer, String> nombresUsuario =
                usuarioRepository.findAllById(idsUsuario)
                        .stream()
                        .collect(Collectors.toMap(
                                UsuarioModel::getId,
                                UsuarioModel::getNombre
                        ));

        Map<Integer, ConsultorioModel> consultoriosPorId =
                consultorioRepository.findAllById(idsConsultorio)
                        .stream()
                        .collect(Collectors.toMap(
                                ConsultorioModel::getId,
                                c -> c
                        ));

        for (CitaModel cita : citas) {

            if (cita.getId_medico() == null) {
                continue;
            }

            MedicoModel medico =
                    medicosPorId.get(cita.getId_medico());

            if (medico == null) {
                continue;
            }

            cita.setNombreMedico(
                    nombresUsuario.get(medico.getId_usuario())
            );

            ConsultorioModel consultorio =
                    consultoriosPorId.get(medico.getId_consultorio());

            if (consultorio != null) {

                cita.setNombreConsultorio(consultorio.getNombre());

                cita.setUbicacionConsultorio(
                        consultorio.getUbicacion()
                );
            }
        }
    }


    // =========================================================
    // CONSULTAR TODAS LAS CITAS
    // =========================================================

    @GetMapping
    @SoloAdministracion
    public ArrayList<CitaModel> get() {

        ArrayList<CitaModel> citas = citaService.getCita();

        enriquecerConDatosPaciente(citas);
        enriquecerConDatosMedico(citas);

        return citas;
    }


    // =========================================================
    // CONSULTAR CITA POR ID
    // =========================================================

    @GetMapping(path = "/{id}")
    @SoloAdministracion
    public CitaModel getCitaById(@PathVariable("id") int id) {

        CitaModel cita = citaService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada"
                ));

        List<CitaModel> unica = List.of(cita);

        enriquecerConDatosPaciente(unica);
        enriquecerConDatosMedico(unica);

        return cita;
    }


    // =========================================================
    // CONSULTAR CITAS POR ESTADO
    // =========================================================

    @GetMapping("/estado/{estado}")
    @SoloAdministracion
    public List<CitaModel> getByEstado(
            @PathVariable String estado
    ) {

        List<CitaModel> citas = citaService.getByEstado(estado);

        enriquecerConDatosPaciente(citas);
        enriquecerConDatosMedico(citas);

        return citas;
    }


    // =========================================================
    // CONSULTAR CITAS DE UN MÉDICO POR ESTADO
    // =========================================================

    @GetMapping("/medico/{idMedico}/estado/{estado}")
    public List<CitaModel> getByMedicoYEstado(
            @PathVariable Integer idMedico,
            @PathVariable String estado
    ) {

        exigirAccesoAMedico(idMedico);

        List<CitaModel> citas =
                citaService.getByMedicoYEstado(idMedico, estado);

        enriquecerConDatosPaciente(citas);
        enriquecerConDatosMedico(citas);

        return citas;
    }


    // =========================================================
    // CONSULTAR HORAS OCUPADAS
    // =========================================================

    @GetMapping("/disponibilidad")
    @SoloAdministracion
    public List<String> getHorasOcupadas(

            @RequestParam Integer idMedico,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha

    ) {

        return citaService.getHorasOcupadas(idMedico, fecha)
                .stream()
                .map(LocalTime::toString)
                .toList();
    }


    // =========================================================
    // CONVERTIR TIPO DE DOCUMENTO
    // =========================================================

    private PacienteModel.TipoDocumento parseTipoDocumento(
            String valor
    ) {

        if (valor == null) {
            return null;
        }

        try {

            return PacienteModel.TipoDocumento.valueOf(
                    valor.trim().toUpperCase()
            );

        } catch (IllegalArgumentException ex) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "tipo_documento inválido. Use uno de: CC, TI, PASAPORTE"
            );
        }
    }


    // =========================================================
    // CONVERTIR EPS
    // =========================================================

    private PacienteModel.Eps parseEps(String valor) {

        if (valor == null) {
            return null;
        }

        try {

            return PacienteModel.Eps.valueOf(
                    valor.trim().toUpperCase()
            );

        } catch (IllegalArgumentException ex) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "eps inválida. Use uno de: SURA, SANITAS, SAVIASALUD"
            );
        }
    }


    // =========================================================
    // CREAR CITA
    // =========================================================

    @PostMapping
    public CitaModel post(@RequestBody CitaModel cita) {

        boolean esPersonal = sesion.esAdministracion();

        // Evitar que el frontend envíe un ID de una cita existente.
        cita.setId(0);

        // -----------------------------------------------------
        // CONFIGURAR SOLICITUD PÚBLICA
        // -----------------------------------------------------

        if (!esPersonal) {
            cita.setEstado(CitaModel.Estado.SOLICITADA);
            cita.setId_medico(null);
            cita.setFecha(null);
            cita.setHora(null);
            cita.setObservaciones(null);
        }

        // -----------------------------------------------------
        // VALIDAR DOCUMENTO
        // -----------------------------------------------------

        String documento = cita.getDocumento();

        if (documento == null || documento.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El documento del paciente es obligatorio"
            );
        }

        documento = documento.trim();

        // -----------------------------------------------------
        // BUSCAR PACIENTE POR DOCUMENTO
        // -----------------------------------------------------

        Optional<PacienteModel> optionalPaciente =
                pacienteRepository.findByDocumento(documento);

        boolean pacienteNuevo = optionalPaciente.isEmpty();

        PacienteModel paciente;

        if (pacienteNuevo) {

            // PRIMER REGISTRO: validar consentimiento.
            if (!esPersonal
                    && !Boolean.TRUE.equals(cita.getAcepta_notificaciones())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Debes aceptar la autorización de tratamiento de datos para enviar la solicitud"
                );
            }

            paciente = new PacienteModel();

            paciente.setDocumento(documento);
            paciente.setNombre(cita.getNombre());
            paciente.setEmail(cita.getEmail());
            paciente.setTelefono(cita.getTelefono());

            PacienteModel.TipoDocumento tipoDoc =
                    parseTipoDocumento(cita.getTipo_documento());

            if (tipoDoc == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "tipo_documento es obligatorio"
                );
            }

            paciente.setTipo_documento(tipoDoc);

            PacienteModel.Eps eps = parseEps(cita.getEps());

            if (eps == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "eps es obligatoria"
                );
            }

            paciente.setEps(eps);

            boolean acepta =
                    Boolean.TRUE.equals(cita.getAcepta_notificaciones());

            paciente.setAcepta_notificaciones(acepta);

            paciente.setFecha_consentimiento(
                    acepta ? LocalDateTime.now() : null
            );

            paciente.setActivo(true);

        } else {

            // PACIENTE EXISTENTE: conservar sus datos originales.
            paciente = optionalPaciente.get();

            // Reactivar si estaba inactivo, sin modificar sus datos personales.
            if (!Boolean.TRUE.equals(paciente.getActivo())) {
                paciente.setActivo(true);
                paciente.setFecha_actualizacion(LocalDateTime.now());
            }
        }

        // -----------------------------------------------------
        // VALIDAR CONFLICTO DE HORARIO
        // -----------------------------------------------------

        if (cita.getId_medico() != null
                && cita.getFecha() != null
                && cita.getHora() != null
                && citaService.existeConflictoHorario(
                cita.getId_medico(),
                cita.getFecha(),
                cita.getHora(),
                0
        )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El médico ya tiene una cita asignada en esa fecha y hora"
            );
        }

        // -----------------------------------------------------
        // ASOCIAR LOS DATOS ORIGINALES DEL PACIENTE A LA CITA
        // -----------------------------------------------------

        cita.setId_paciente(paciente.getId());

        cita.setNombre(paciente.getNombre());
        cita.setEmail(paciente.getEmail());
        cita.setTelefono(paciente.getTelefono());

        cita.setTipo_documento(
                paciente.getTipo_documento() != null
                        ? paciente.getTipo_documento().name()
                        : null
        );

        cita.setDocumento(paciente.getDocumento());

        cita.setEps(
                paciente.getEps() != null
                        ? paciente.getEps().name()
                        : null
        );

        cita.setActivo(true);

        // -----------------------------------------------------
        // GUARDAR PACIENTE Y CITA
        // -----------------------------------------------------

        CitaModel guardada =
                citaService.postCitaConPaciente(paciente, cita);

        // Procesar notificaciones.
        notificaciones.procesarCambio(null, guardada);

        return guardada;
    }


    // =========================================================
    // ACTUALIZAR CITA
    // =========================================================

    @PutMapping(path = "/{id}")
    public CitaModel put(
            @RequestBody CitaModel request,
            @PathVariable("id") int id
    ) {

        if (!sesion.estaAutenticado()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Sesión requerida"
            );
        }

        if (sesion.esMedico()) {
            return actualizarEstadoComoMedico(request, id);
        }

        if (sesion.esAdministracion()) {
            return putAdministracion(request, id);
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No tienes permisos para realizar esta acción"
        );
    }


    // =========================================================
    // ACTUALIZAR CITA COMO ADMINISTRADOR
    // =========================================================

    private CitaModel putAdministracion(
            CitaModel request,
            int id
    ) {

        CitaModel actual = citaService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada"
                ));

        NotificacionService.EstadoCita antes =
                NotificacionService.EstadoCita.de(actual);


        // Validar conflicto de horario.
        if (request.getId_medico() != null
                && request.getFecha() != null
                && request.getHora() != null) {

            boolean conflicto = citaService.existeConflictoHorario(
                    request.getId_medico(),
                    request.getFecha(),
                    request.getHora(),
                    id
            );

            if (conflicto) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El médico ya tiene una cita asignada en esa fecha y hora"
                );
            }
        }


        // Solo se modifica el paciente si se envía un documento.
        if (request.getDocumento() != null
                && !request.getDocumento().isBlank()) {

            Optional<PacienteModel> optionalPaciente =
                    pacienteRepository.findByDocumento(
                            request.getDocumento()
                    );

            PacienteModel paciente;

            if (optionalPaciente.isPresent()) {

                paciente = optionalPaciente.get();

            } else {

                paciente = new PacienteModel();
                paciente.setDocumento(request.getDocumento());
                paciente.setActivo(true);
            }


            // Actualizar los datos recibidos.
            var nombre = Optional.ofNullable(request.getNombre())
                    .orElse(paciente.getNombre());

            var email = Optional.ofNullable(request.getEmail())
                    .orElse(paciente.getEmail());

            var telefono = Optional.ofNullable(request.getTelefono())
                    .orElse(paciente.getTelefono());

            paciente.setNombre(nombre);
            paciente.setEmail(email);
            paciente.setTelefono(telefono);

            PacienteModel.TipoDocumento tipoDoc =
                    parseTipoDocumento(request.getTipo_documento());

            if (tipoDoc != null) {
                paciente.setTipo_documento(tipoDoc);
            }

            PacienteModel.Eps eps = parseEps(request.getEps());

            if (eps != null) {
                paciente.setEps(eps);
            }

            // Si el paciente estaba inactivo y administración
            // vuelve a trabajar con él, se reactiva.
            paciente.setActivo(true);

            paciente = pacienteRepository.save(paciente);


            // Asociar el paciente actualizado a la cita.
            request.setId_paciente(paciente.getId());

            request.setNombre(paciente.getNombre());
            request.setEmail(paciente.getEmail());
            request.setTelefono(paciente.getTelefono());

            request.setTipo_documento(
                    paciente.getTipo_documento() != null
                            ? paciente.getTipo_documento().name()
                            : null
            );

            request.setEps(
                    paciente.getEps() != null
                            ? paciente.getEps().name()
                            : null
            );
        }


        CitaModel guardada = citaService.putCita(request, id);

        notificaciones.procesarCambio(antes, guardada);

        return guardada;
    }


    // =========================================================
    // ELIMINAR CITA
    // =========================================================

    @DeleteMapping(path = "/{id}")
    @SoloAdministracion
    public String delete(@PathVariable("id") int id) {

        boolean ok = citaService.deleteCita(id);

        return ok
                ? "Se eliminó la cita con id " + id
                : "No se pudo eliminar la cita con id " + id;
    }
}