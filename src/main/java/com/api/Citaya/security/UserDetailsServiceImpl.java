package com.api.Citaya.security;

import com.api.Citaya.models.UsuarioModel;
import com.api.Citaya.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .disabled(usuario.getActivo() == null || !usuario.getActivo())
                .authorities("ROLE_" + usuario.getRol().name())
                .build();
    }
}