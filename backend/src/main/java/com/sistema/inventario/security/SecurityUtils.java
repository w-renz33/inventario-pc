package com.sistema.inventario.security;

import com.sistema.inventario.auth.entity.Usuario;
import com.sistema.inventario.auth.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;

    public SecurityUtils(UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Usuario de la peticion en curso, o null si no hay sesion.
     * El contexto puede venir sin Authentication (peticiones publicas) y el
     * nombre puede ser "anonymousUser", caso en que tampoco hay usuario real.
     */
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        String username = authentication.getName();
        if (username == null || username.isBlank() || "anonymousUser".equals(username)) {
            return null;
        }
        return usuarioRepository.findByUsername(username).orElse(null);
    }
}
