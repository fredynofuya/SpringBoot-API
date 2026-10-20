package com.api.Citaya.correo;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
@ConditionalOnProperty(name = "app.correo.modo", havingValue = "smtp", matchIfMissing = true)
public class CorreoSmtpService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(CorreoSmtpService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String remitente;

    // @Async: la respuesta HTTP no espera al servidor de correo, así el tiempo de respuesta
    // no revela si el correo existía o no.
    @Async
    @Override
    public void enviarRecuperacion(String destinatario, String nombre, String enlace, int minutosVigencia) {
        try {
            String texto = "Hola " + nombre + ",\n\n"
                    + "Recibimos una solicitud para restablecer la contraseña de tu cuenta de MedSoftIA.\n"
                    + "Usa este enlace (un solo uso, vence en " + minutosVigencia + " minutos):\n"
                    + enlace + "\n\n"
                    + "Si no fuiste tú, ignora este correo: tu contraseña no cambiará.";

            String html = """
                    <div style="font-family:Arial,sans-serif;max-width:480px;margin:auto;color:#2f3e44">
                      <h2 style="color:#568281">MedSoftIA</h2>
                      <p>Hola %s,</p>
                      <p>Recibimos una solicitud para restablecer la contraseña de tu cuenta.
                         Este enlace es de un solo uso y vence en %d minutos.</p>
                      <p style="text-align:center;margin:28px 0">
                        <a href="%s" style="background:#1a7f8c;color:#fff;padding:12px 22px;border-radius:8px;text-decoration:none;font-weight:bold">Crear nueva contraseña</a>
                      </p>
                      <p style="font-size:13px;color:#6b7a7a">Si el botón no funciona, copia y pega esta dirección en tu navegador:<br>%s</p>
                      <p style="font-size:13px;color:#6b7a7a">Si no fuiste tú, ignora este correo: tu contraseña no cambiará.</p>
                    </div>
                    """.formatted(HtmlUtils.htmlEscape(nombre), minutosVigencia, enlace, enlace);

            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente, "MedSoftIA");
            helper.setTo(destinatario);
            helper.setSubject("Recupera tu contraseña de MedSoftIA");
            helper.setText(texto, html);
            mailSender.send(mensaje);
        } catch (Exception e) {
            // No se registra el enlace ni se propaga el error: el usuario recibe la misma respuesta siempre.
            log.error("No se pudo enviar el correo de recuperación: {}", e.getMessage());
        }
    }
}