package com.api.Citaya.dto;

public final class PasswordDTO {
    private PasswordDTO() {}

    public record Cambio(String passwordActual, String passwordNueva) {}
    public record Solicitud(String email) {}
    public record Token(String token) {}
    public record Restablecer(String token, String passwordNueva) {}
}