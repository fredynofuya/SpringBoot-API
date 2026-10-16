package com.api.Citaya.dto;

public class MedicoEdicionDTO {
    private String nombre;
    private String email;
    private Integer id_especialidad;
    private Integer id_consultorio;
    private String registro_profesional;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getId_especialidad() { return id_especialidad; }
    public void setId_especialidad(Integer id_especialidad) { this.id_especialidad = id_especialidad; }
    public Integer getId_consultorio() { return id_consultorio; }
    public void setId_consultorio(Integer id_consultorio) { this.id_consultorio = id_consultorio; }
    public String getRegistro_profesional() { return registro_profesional; }
    public void setRegistro_profesional(String registro_profesional) { this.registro_profesional = registro_profesional; }
}
