package com.api.Citaya.models;

import jakarta.persistence.*;

// Modelo mínimo de solo lectura. No incluye password/rol todavía porque el módulo
// de autenticación no está implementado (ver riesgos del documento de diseño del proyecto).
@Entity
@Table(name = "Usuarios")
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Usuario")
    private int id;

    @Column(name = "Nombre")
    private String nombre;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

