package com.api.Citaya.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Password_Reset_Tokens")
public class PasswordResetTokenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Token")
    private int id;

    @Column(name = "Id_Usuario", nullable = false)
    private int usuarioId;

    // SHA-256 del token (64 caracteres hex). El token en claro solo viaja en el correo.
    @Column(name = "Token_Hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "Creado_En", nullable = false)
    private LocalDateTime creadoEn;

    @Column(name = "Expira_En", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "Usado_En")
    private LocalDateTime usadoEn;

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
    public LocalDateTime getExpiraEn() { return expiraEn; }
    public void setExpiraEn(LocalDateTime expiraEn) { this.expiraEn = expiraEn; }
    public LocalDateTime getUsadoEn() { return usadoEn; }
    public void setUsadoEn(LocalDateTime usadoEn) { this.usadoEn = usadoEn; }
}