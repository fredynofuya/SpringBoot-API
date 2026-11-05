package com.api.Citaya.correo;

import java.util.concurrent.CompletableFuture;

public interface EmailService {

    void enviarRecuperacion(String destinatario, String nombre, String enlace, int minutosVigencia);

    // true = el servidor de correo aceptó el mensaje; false = falló (el detalle queda en el log).
    CompletableFuture<Boolean> enviar(String destinatario, String asunto, String texto, String html);
}