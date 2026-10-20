package com.api.Citaya.correo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// SOLO DESARROLLO: escribe el enlace en el log en vez de enviar un correo.
@Service
@ConditionalOnProperty(name = "app.correo.modo", havingValue = "consola")
public class CorreoConsolaService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(CorreoConsolaService.class);

    @Override
    public void enviarRecuperacion(String destinatario, String nombre, String enlace, int minutosVigencia) {
        log.warn("[MODO CONSOLA - solo desarrollo] Enlace de recuperación para {} (vence en {} min): {}",
                destinatario, minutosVigencia, enlace);
    }
}