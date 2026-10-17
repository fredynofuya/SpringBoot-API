package com.api.Citaya.controllers;

import com.api.Citaya.dto.*;
import com.api.Citaya.security.SoloSuperAdmin;
import com.api.Citaya.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@SoloSuperAdmin   // toda la clase: solo el super administrador gestiona usuarios
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResumenDTO> get() {
        return usuarioService.listar();
    }

    @PostMapping
    public UsuarioResumenDTO post(@RequestBody UsuarioRegistroDTO dto) {
        return usuarioService.crear(dto);
    }

    @PutMapping("/{id}")
    public UsuarioResumenDTO put(@PathVariable int id, @RequestBody UsuarioEdicionDTO dto) {
        return usuarioService.editar(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public UsuarioResumenDTO cambiarEstado(@PathVariable int id, @RequestBody UsuarioEstadoDTO dto) {
        return usuarioService.cambiarEstado(id, dto.isActivo());
    }
}