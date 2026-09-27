package com.api.Citaya.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Consultorios")
public class ConsultorioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Consultorio")
    private int id;

    @Column(name = "Nombre")
    private String nombre;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
