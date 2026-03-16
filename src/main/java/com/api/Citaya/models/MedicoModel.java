package com.api.Citaya.models;

import jakarta.persistence.*;

@Entity
@Table (name = "Medicos")
public class MedicoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Medico")
    private int id;

    @Column(name = "Id_Usuario")
    private int id_usuario;

    @Column (name = "Id_Especialidad")
    private int id_especialidad;

    @Column (name = "registro_Profesional")
    private  String registro_profesional;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_especialidad() {
        return id_especialidad;
    }

    public void setId_especialidad(int id_especialidad) {
        this.id_especialidad = id_especialidad;
    }

    public String getRegistro_profesional() {
        return registro_profesional;
    }

    public void setRegistro_profesional(String registro_profesional) {
        this.registro_profesional = registro_profesional;
    }
}
