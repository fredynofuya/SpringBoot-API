package com.api.Citaya.correo;

import com.api.Citaya.models.NotificacionModel;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Contenido mínimo a propósito: fecha, hora, médico y lugar. Nunca motivo, documento, EPS ni observaciones.
public final class PlantillasCorreo {

    private PlantillasCorreo() {}

    public record DatosCita(
            String institucion, String telefono, String paciente,
            LocalDate fecha, LocalTime hora,
            String medico, String consultorio, String ubicacion,
            LocalDate fechaAnterior, LocalTime horaAnterior) {}

    public record Correo(String asunto, String texto, String html) {}

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale.of("es", "CO"));

    public static Correo construir(NotificacionModel.Tipo tipo, DatosCita d) {
        return switch (tipo) {
            case SOLICITUD_RECIBIDA -> armar(d, "Recibimos tu solicitud de cita",
                    "Recibimos tu solicitud de cita. Un agente se pondrá en contacto contigo en las próximas 24 horas "
                            + "para confirmar la fecha, la hora y el médico.",
                    List.of(),
                    "Si necesitas comunicarte con nosotros, llama al " + d.telefono() + ".");

            case CONFIRMADA -> armar(d, "Tu cita está confirmada",
                    "Tu cita quedó confirmada con los siguientes datos:",
                    detallesCita(d, false),
                    avisoCambios(d));

            case REPROGRAMADA -> armar(d, "Tu cita fue reprogramada",
                    "Tu cita fue reprogramada. Estos son los nuevos datos:",
                    detallesCita(d, true),
                    avisoCambios(d));

            case CANCELADA -> armar(d, "Tu cita fue cancelada",
                    d.fecha() != null
                            ? "Tu cita del " + fecha(d.fecha()) + " a las " + hora(d.hora()) + " fue cancelada."
                            : "Tu solicitud de cita fue cancelada.",
                    List.of(),
                    "Si quieres reprogramarla, comunícate con nosotros al " + d.telefono() + ".");

            case RECORDATORIO -> armar(d, "Recordatorio: tu cita es mañana",
                    "Te recordamos tu cita de mañana:",
                    detallesCita(d, false),
                    "Si no puedes asistir, avísanos al " + d.telefono() + ".");
        };
    }

    /* ---------- Piezas ---------- */

    private static List<String[]> detallesCita(DatosCita d, boolean conAnterior) {
        List<String[]> filas = new ArrayList<>();
        if (d.fecha() != null) filas.add(new String[]{conAnterior ? "Nueva fecha" : "Fecha", fecha(d.fecha())});
        if (d.hora() != null) filas.add(new String[]{conAnterior ? "Nueva hora" : "Hora", hora(d.hora())});
        if (d.medico() != null) filas.add(new String[]{"Médico", d.medico()});
        if (d.consultorio() != null) {
            String lugar = d.ubicacion() != null && !d.ubicacion().isBlank()
                    ? d.consultorio() + " · " + d.ubicacion() : d.consultorio();
            filas.add(new String[]{"Consultorio", lugar});
        }
        if (conAnterior && d.fechaAnterior() != null && d.horaAnterior() != null) {
            filas.add(new String[]{"Horario anterior", fecha(d.fechaAnterior()) + ", " + hora(d.horaAnterior())});
        }
        return filas;
    }

    private static String avisoCambios(DatosCita d) {
        return "Si no puedes asistir, comunícate con nosotros al " + d.telefono() + " para cancelarla o reprogramarla.";
    }

    private static String pie(DatosCita d) {
        return "Recibes este mensaje porque autorizaste notificaciones sobre tus citas en " + d.institucion()
                + ". Si ya no deseas recibirlas, comunícate al " + d.telefono() + ".";
    }

    private static Correo armar(DatosCita d, String asunto, String intro, List<String[]> detalles, String cierre) {
        // Texto plano
        StringBuilder t = new StringBuilder("Hola ").append(d.paciente()).append(",\n\n").append(intro).append("\n");
        if (!detalles.isEmpty()) {
            t.append("\n");
            for (String[] f : detalles) t.append("- ").append(f[0]).append(": ").append(f[1]).append("\n");
        }
        t.append("\n").append(cierre).append("\n\n").append(pie(d));

        // HTML (todo lo variable se escapa)
        StringBuilder tabla = new StringBuilder();
        if (!detalles.isEmpty()) {
            tabla.append("<table style=\"border-collapse:collapse;margin:14px 0;font-size:14px\">");
            for (String[] f : detalles) {
                tabla.append("<tr><td style=\"padding:6px 16px 6px 0;color:#6b7a7a\">").append(esc(f[0]))
                        .append("</td><td style=\"padding:6px 0;font-weight:bold\">").append(esc(f[1]))
                        .append("</td></tr>");
            }
            tabla.append("</table>");
        }
        String html = "<div style=\"font-family:Arial,sans-serif;max-width:520px;margin:auto;color:#2f3e44\">"
                + "<h2 style=\"color:#568281\">" + esc(d.institucion()) + "</h2>"
                + "<p>Hola " + esc(d.paciente()) + ",</p>"
                + "<p>" + esc(intro) + "</p>"
                + tabla
                + "<p>" + esc(cierre) + "</p>"
                + "<p style=\"font-size:12px;color:#6b7a7a;border-top:1px solid #e3eeed;padding-top:12px\">"
                + esc(pie(d)) + "</p></div>";

        return new Correo(asunto + " · " + d.institucion(), t.toString(), html);
    }

    private static String fecha(LocalDate f) {
        return f == null ? "" : FECHA.format(f);
    }

    // 12 horas escrito a mano: evita las diferencias de a. m./p. m. entre versiones de Java.
    private static String hora(LocalTime h) {
        if (h == null) return "";
        int h12 = h.getHour() % 12 == 0 ? 12 : h.getHour() % 12;
        return h12 + ":" + String.format("%02d", h.getMinute()) + (h.getHour() >= 12 ? " p. m." : " a. m.");
    }

    private static String esc(String s) {
        return HtmlUtils.htmlEscape(s == null ? "" : s);
    }
}