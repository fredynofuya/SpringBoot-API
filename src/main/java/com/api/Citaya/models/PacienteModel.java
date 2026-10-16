package com.api.Citaya.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

// La anotación @Entity se utiliza para indicar que esta clase es una entidad de JPA (Java Persistence API) y se mapeará a una tabla en la base de datos.
@Entity
@Table (name = "Pacientes")
public class PacienteModel {
    // Atributos de la clase PacienteModel
    // La anotación @GeneratedValue se utiliza para indicar que el valor del campo "id" se generará automáticamente
    // por la base de datos.
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "Id_Paciente")
    private int id;

    @Column (name = "Nombre")
    private String nombre;

    @Column (name = "Email")
    private String email;

    public enum TipoDocumento {
        CC, TI, PASAPORTE
    }

    @Column (name = "Tipo_Documento")
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipo_documento;

    @Column (name = "Documento")
    private String documento;

    @Column (name = "Telefono")
    private String telefono;

    public enum Eps {
        SURA, SANITAS, SAVIASALUD
    }

    @Column (name = "Eps")
    @Enumerated(EnumType.STRING)
    private Eps eps;

    @CreationTimestamp
    @Column(name = "Fecha_Registro", updatable = false)
    private LocalDateTime fecha_registro;


    @UpdateTimestamp
    @Column(name = "Fecha_Actualizacion")
    private LocalDateTime fecha_actualizacion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public TipoDocumento getTipo_documento() {
        return tipo_documento;
    }

    public void setTipo_documento(TipoDocumento tipo_documento) {
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

    public Eps getEps() { return eps; }
    public void setEps(Eps eps) { this.eps = eps; }

    public LocalDateTime getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(LocalDateTime fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public LocalDateTime getFecha_actualizacion() {
        return fecha_actualizacion;
    }

    public void setFecha_actualizacion(LocalDateTime fecha_actualizacion) {
        this.fecha_actualizacion = fecha_actualizacion;
    }
}
