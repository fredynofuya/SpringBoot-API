package com.api.Citaya.services;

import com.api.Citaya.correo.EmailService;
import com.api.Citaya.correo.PlantillasCorreo;
import com.api.Citaya.models.*;
import com.api.Citaya.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);
    private static final String EMAIL_REGEX = "^\\S+@\\S+\\.\\S+$";

    // Foto de lo que importa de una cita para detectar cambios. Durante la petición JPA comparte
    // la misma entidad, así que hay que copiarla ANTES de modificarla.
    public record EstadoCita(CitaModel.Estado estado, Integer idMedico, LocalDate fecha, LocalTime hora) {
        public static EstadoCita de(CitaModel c) {
            return new EstadoCita(c.getEstado(), c.getId_medico(), c.getFecha(), c.getHora());
        }
    }

    @Value("${app.institucion.nombre}")
    private String institucion;

    @Value("${app.institucion.telefono}")
    private String telefono;

    @Autowired private IPacienteRepository pacienteRepository;
    @Autowired private IMedicoRepository medicoRepository;
    @Autowired private IUsuarioRepository usuarioRepository;
    @Autowired private IConsultorioRepository consultorioRepository;
    @Autowired private INotificacionRepository notificacionRepository;
    @Autowired private EmailService emailService;

    // Compara el estado anterior con el nuevo y notifica si corresponde.
    // Nunca lanza excepciones: una notificación fallida no debe romper la operación sobre la cita.
    public void procesarCambio(EstadoCita antes, CitaModel despues) {
        try {
            NotificacionModel.Tipo tipo = determinarTipo(antes, despues);
            if (tipo != null) notificar(tipo, despues, antes);
        } catch (Exception e) {
            log.error("No se pudo procesar la notificación de la cita {}: {}", despues.getId(), e.getMessage());
        }
    }

    // Reglas de negocio de qué evento corresponde. Pública y estática para probarla sin Spring.
    public static NotificacionModel.Tipo determinarTipo(EstadoCita antes, CitaModel despues) {
        CitaModel.Estado nuevo = despues.getEstado();

        if (antes == null) { // cita recién creada
            if (nuevo == CitaModel.Estado.SOLICITADA) return NotificacionModel.Tipo.SOLICITUD_RECIBIDA;
            if (nuevo == CitaModel.Estado.CONFIRMADA) return NotificacionModel.Tipo.CONFIRMADA;
            return null;
        }

        CitaModel.Estado previo = antes.estado();

        if (nuevo == CitaModel.Estado.CANCELADA && previo != CitaModel.Estado.CANCELADA) {
            return NotificacionModel.Tipo.CANCELADA;
        }
        if (nuevo == CitaModel.Estado.CONFIRMADA) {
            // Volver de ATENDIDA a CONFIRMADA es una corrección interna: no se notifica.
            if (previo == CitaModel.Estado.SOLICITADA || previo == CitaModel.Estado.CANCELADA) {
                return NotificacionModel.Tipo.CONFIRMADA;
            }
            if (previo == CitaModel.Estado.CONFIRMADA && cambioAgenda(antes, despues)) {
                return NotificacionModel.Tipo.REPROGRAMADA;
            }
        }
        return null;
    }

    private static boolean cambioAgenda(EstadoCita antes, CitaModel despues) {
        return !Objects.equals(antes.idMedico(), despues.getId_medico())
                || !Objects.equals(antes.fecha(), despues.getFecha())
                || !Objects.equals(antes.hora(), despues.getHora());
    }

    private void notificar(NotificacionModel.Tipo tipo, CitaModel cita, EstadoCita antes) {
        Optional<PacienteModel> encontrado = pacienteRepository.findById(cita.getId_paciente());
        if (encontrado.isEmpty()) return;
        PacienteModel paciente = encontrado.get();

        if (!Boolean.TRUE.equals(paciente.getAcepta_notificaciones())) {
            registrar(cita.getId(), tipo, NotificacionModel.Estado.OMITIDA, "SIN_CONSENTIMIENTO");
            return;
        }
        String email = paciente.getEmail() == null ? "" : paciente.getEmail().trim();
        if (!email.matches(EMAIL_REGEX)) {
            registrar(cita.getId(), tipo, NotificacionModel.Estado.OMITIDA, "SIN_CORREO_VALIDO");
            return;
        }

        PlantillasCorreo.Correo correo = PlantillasCorreo.construir(tipo, construirDatos(paciente, cita, antes));
        int idCita = cita.getId();

        emailService.enviar(email, correo.asunto(), correo.texto(), correo.html())
                .whenComplete((ok, error) -> {
                    boolean enviado = error == null && Boolean.TRUE.equals(ok);
                    registrar(idCita, tipo,
                            enviado ? NotificacionModel.Estado.ENVIADA : NotificacionModel.Estado.FALLIDA,
                            enviado ? null : "ERROR_ENVIO");
                });
    }

    private PlantillasCorreo.DatosCita construirDatos(PacienteModel paciente, CitaModel cita, EstadoCita antes) {
        String medico = null;
        String consultorio = null;
        String ubicacion = null;

        if (cita.getId_medico() != null) {
            Optional<MedicoModel> m = medicoRepository.findById(cita.getId_medico());
            if (m.isPresent()) {
                medico = usuarioRepository.findById(m.get().getId_usuario()).map(UsuarioModel::getNombre).orElse(null);
                Optional<ConsultorioModel> c = consultorioRepository.findById(m.get().getId_consultorio());
                if (c.isPresent()) {
                    consultorio = c.get().getNombre();
                    ubicacion = c.get().getUbicacion();
                }
            }
        }

        return new PlantillasCorreo.DatosCita(
                institucion, telefono, paciente.getNombre(),
                cita.getFecha(), cita.getHora(),
                medico, consultorio, ubicacion,
                antes != null ? antes.fecha() : null,
                antes != null ? antes.hora() : null);
    }

    private void registrar(int idCita, NotificacionModel.Tipo tipo, NotificacionModel.Estado estado, String detalle) {
        try {
            NotificacionModel n = new NotificacionModel();
            n.setIdCita(idCita);
            n.setTipo(tipo);
            n.setCanal(NotificacionModel.Canal.EMAIL);
            n.setEstado(estado);
            n.setDetalle(detalle);
            n.setFecha(LocalDateTime.now());
            notificacionRepository.save(n);
        } catch (Exception e) {
            log.error("No se pudo registrar la notificación de la cita {}: {}", idCita, e.getMessage());
        }
    }
}