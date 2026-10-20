package com.api.Citaya.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UsuarioPrincipal extends User {

    private final int versionToken;

    public UsuarioPrincipal(String username, String password, boolean enabled,
                            int versionToken, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
        this.versionToken = versionToken;
    }

    public int getVersionToken() { return versionToken; }
}