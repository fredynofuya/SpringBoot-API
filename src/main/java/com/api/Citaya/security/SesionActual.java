package com.api.Citaya.security;

import com.api.Citaya.models.MedicoModel;
import com.api.Citaya.models.UsuarioModel;
import com.api.Citaya.repositories.IMedicoRepository;
import com.api.Citaya.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SesionActual {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IMedicoRepository medicoRepository;

    private Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean estaAutenticado() {
        Authentication a = auth();
        return a != null && a.isAuthenticated() && !(a instanceof AnonymousAuthenticationToken);
    }

    private boolean tieneRol(String rol) {
        return estaAutenticado() && auth().getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ROLE_" + rol));
    }

    public boolean esAdministracion() { return tieneRol("ADMIN") || tieneRol("SUPER_ADMIN"); }
    public boolean esSuperAdmin()     { return tieneRol("SUPER_ADMIN"); }
    public boolean esMedico()         { return tieneRol("MEDICO"); }

    public Optional<Integer> idUsuarioActual() {
        if (!estaAutenticado()) return Optional.empty();
        return usuarioRepository.findByEmail(auth().getName()).map(UsuarioModel::getId);
    }

    public Optional<Integer> idMedicoActual() {
        return idUsuarioActual()
                .flatMap(medicoRepository::buscarPorUsuario)
                .map(MedicoModel::getId);
    }
}