//package com.api.Citaya.dto;
//
//public class LoginResponse {
//    private String token;
//    private int id;
//    private String nombre;
//    private String email;
//    private String rol;
//
//    public LoginResponse(String token, int id, String nombre, String email, String rol) {
//        this.token = token;
//        this.id = id;
//        this.nombre = nombre;
//        this.email = email;
//        this.rol = rol;
//    }
//
//    public String getToken() { return token; }
//    public int getId() { return id; }
//    public String getNombre() { return nombre; }
//    public String getEmail() { return email; }
//    public String getRol() { return rol; }
//}
package com.api.Citaya.dto;

public class LoginResponse {
    private String token;
    private int id;
    private String nombre;
    private String email;
    private String rol;
    private Integer idMedico; // null para administradores

    public LoginResponse(String token, int id, String nombre, String email, String rol, Integer idMedico) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.idMedico = idMedico;
    }

    public String getToken() { return token; }
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public Integer getIdMedico() { return idMedico; }
}