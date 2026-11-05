package com.api.Citaya.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notificaciones")
public class NotificacionModel {

    public enum Tipo { SOLICITUD_RECIBIDA, CONFIRMADA, REPROGRAMADA, CANCELADA, RECORDATORIO }
    public enum Canal { EMAIL }
    public enum Estado { ENVIADA, FALLIDA, OMITIDA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Notificacion")
    private int id;

    @Column(name = "Id_Cita", nullable = false)
    private int idCita;

    @Enumerated(EnumType.STRING)
    @Column(name = "Tipo", nullable = false)
    private Tipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "Canal", nullable = false)
    private Canal canal;

    @Enumerated(EnumType.STRING)
    @Column(name = "Estado", nullable = false)
    private Estado estado;

    // Solo códigos cortos (SIN_CONSENTIMIENTO, SIN_CORREO_VALIDO, ERROR_ENVIO): nunca datos personales.
    @Column(name = "Detalle", length = 100)
    private String detalle;

    @Column(name = "Fecha", nullable = false)
    private LocalDateTime fecha;

    public int getId() { return id; }
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }
    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
    public Canal getCanal() { return canal; }
    public void setCanal(Canal canal) { this.canal = canal; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}