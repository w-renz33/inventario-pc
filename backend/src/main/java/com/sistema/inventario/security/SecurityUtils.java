package com.sistema.inventario.security;

import com.sistema.inventario.auth.entity.Usuario;
import com.sistema.inventario.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;

    public SecurityUtils(UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    public Usuario getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (username != null && !username.equals("anonymousUser")) {
            return usuarioRepository.findByUsername(username).orElse(null);
        }
        return null;
    }
}
