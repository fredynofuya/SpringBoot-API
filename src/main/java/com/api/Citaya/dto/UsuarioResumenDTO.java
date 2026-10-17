package com.api.Citaya.dto;

import java.time.LocalDateTime;

public class UsuarioResumenDTO {
    private int id;
    private String nombre;
    private String email;
    private String rol;
    private boolean activo;
    private LocalDateTime fecha_creacion;

    public UsuarioResumenDTO(int id, String nombre, String email, String rol, boolean activo, LocalDateTime fecha_creacion) {
        this.id = id; this.nombre = nombre; this.email = email;
        this.rol = rol; this.activo = activo; this.fecha_creacion = fecha_creacion;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getFecha_creacion() { return fecha_creacion; }
}
