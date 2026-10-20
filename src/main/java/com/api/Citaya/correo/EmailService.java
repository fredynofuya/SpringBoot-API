package com.api.Citaya.correo;

public interface EmailService {
    void enviarRecuperacion(String destinatario, String nombre, String enlace, int minutosVigencia);
}