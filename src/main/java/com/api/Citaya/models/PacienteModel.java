package com.api.Citaya.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table (name = "Pacientes")
public class PacienteModel {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "Id_Paciente")
    private int id;

    @Column (name = "Nombre")
    private String nombre;

    @Column (name = "Email")
    private String email;

    @Column (name = "Tipo_Documento")
    private String tipo_documento;

    @Column (name = "Documento")
    private String documento;

    @Column (name = "Telefono")
    private String telefono;

    @Column (name = "Eps")
    private String eps;

    @Column (name = "Fecha_Registro")
    private Date fecha_registro;

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

    public Date getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro;
    }
}
