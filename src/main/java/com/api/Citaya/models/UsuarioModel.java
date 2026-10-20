package com.api.Citaya.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "Usuarios")
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Usuario")
    private int id;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Email", unique = true)
    private String email;

    // Siempre almacena el hash (BCrypt), nunca texto plano.
    @Column(name = "Password")
    private String password;

    public enum Rol { SUPER_ADMIN, ADMIN, MEDICO }

    @Column(name = "Rol")
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(name = "Activo")
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "Fecha_Creacion", updatable = false)
    private LocalDateTime fecha_creacion;

    @Column(name = "Version_Token")
    private Integer versionToken = 0;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFecha_creacion() { return fecha_creacion; }
    public void setFecha_creacion(LocalDateTime fecha_creacion) { this.fecha_creacion = fecha_creacion; }

    public int getVersionToken() { return versionToken == null ? 0 : versionToken; }
    public void setVersionToken(int versionToken) { this.versionToken = versionToken; }
}

