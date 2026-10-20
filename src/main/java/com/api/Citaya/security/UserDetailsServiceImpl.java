//package com.api.Citaya.security;
//
//import com.api.Citaya.models.UsuarioModel;
//import com.api.Citaya.repositories.IUsuarioRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    @Autowired
//    private IUsuarioRepository usuarioRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        UsuarioModel usuario = usuarioRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
//
//        return User.builder()
//                .username(usuario.getEmail())
//                .password(usuario.getPassword())
//                .disabled(usuario.getActivo() == null || !usuario.getActivo())
//                .authorities("ROLE_" + usuario.getRol().name())
//                .build();
//    }
//}
package com.api.Citaya.security;

import com.api.Citaya.models.UsuarioModel;
import com.api.Citaya.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));

        return new UsuarioPrincipal(
                usuario.getEmail(),
                usuario.getPassword(),
                Boolean.TRUE.equals(usuario.getActivo()),
                usuario.getVersionToken(),
                AuthorityUtils.createAuthorityList("ROLE_" + usuario.getRol().name())
        );
    }
}
