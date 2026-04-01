package com.api.Citaya.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Table (name = "Citas")
public class CitaModel {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "Id_Cita")
    private int id;

    @Column (name = "Id_Paciente")
    private int id_paciente;

    @Column (name = "Id_Medico")
    private int id_medico;

    @Column (name = "Id_Especialidad")
    private int id_especialidad;

    @Column (name = "Id_consultorio")
    private int id_consultorio;

    @Column (name = "Id_documento")
    private int id_documento;

    @Column (name = "Fecha")
    private LocalDate fecha;

    @Column (name = "Hora")
    private LocalTime hora;

    public enum Estado {
        SOLICITADA,
        CONFIRMADA,
        REPROGRAMADA,
        CANCELADA,
        COMPLETADA,
        ATENDIDA
    }

    @Column (name = "Estado")
    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.SOLICITADA;

    @Column (name = "Mensaje")
    private String mensaje;

    @Column (name = "Observaciones")
    private String observaciones;

    @CreationTimestamp
    @Column(name = "Fecha_Creacion", updatable = false)
    private LocalDateTime fecha_creacion;

    @UpdateTimestamp
    @Column(name = "Fecha_Actualizacion")
    private LocalDateTime fecha_actualizacion;

    @Transient
    private String nombre;

    @Transient
    private String email;

    @Transient
    private String tipo_documento;

    @Transient
    private String documento;

    @Transient
    private String telefono;

    @Transient
    private String eps;

    @Transient
    private LocalDateTime fecha_registro;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo_documento() {
        return tipo_documento;
    }

    public void setTipo_documento(String tipo_documento) {
        this.tipo_documento = tipo_documento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public LocalDateTime getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(LocalDateTime fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_paciente() {
        return id_paciente;
    }

    public void setId_paciente(int id_paciente) {
        this.id_paciente = id_paciente;
    }

    public int getId_medico() {
        return id_medico;
    }

    public void setId_medico(int id_medico) {
        this.id_medico = id_medico;
    }

    public int getId_especialidad() {
        return id_especialidad;
    }

    public void setId_especialidad(int id_especialidad) {
        this.id_especialidad = id_especialidad;
    }

    public int getId_consultorio() {
        return id_consultorio;
    }

    public void setId_consultorio(int id_consultorio) {
        this.id_consultorio = id_consultorio;
    }

    public int getId_documento() {
        return id_documento;
    }

    public void setId_documento(int id_documento) {
        this.id_documento = id_documento;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(LocalDateTime fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public LocalDateTime getFecha_actualizacion() {
        return fecha_actualizacion;
    }

    public void setFecha_actualizacion(LocalDateTime fecha_actualizacion) {
        this.fecha_actualizacion = fecha_actualizacion;
    }
}
