package com.sistema.inventario.security;

import com.sistema.inventario.auth.entity.RolPermiso;
import com.sistema.inventario.auth.entity.Usuario;
import com.sistema.inventario.auth.entity.UsuarioRol;
import com.sistema.inventario.auth.repository.AuthDao;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Puente Variante C: Usuario -> UsuarioRol -> Rol -> RolPermiso -> Permiso
 * convierte roles en ROLE_X y permiso.codigo en authorities (recurso.accion).
 * Usa AuthDao (EntityManager, 2 queries con JOIN FETCH) en vez de 1 + N
 * queries encadenadas por repositorios derivados.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthDao authDao;

    public CustomUserDetailsService(AuthDao authDao) {
        this.authDao = authDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = authDao.findUsuarioByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        List<UsuarioRol> roles = authDao.findRolesConRol(usuario.getId());
        List<Long> rolIds = roles.stream().map(ur -> ur.getRol().getId()).distinct().toList();
        for (UsuarioRol ur : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + ur.getRol().getNombre()));
        }
        for (RolPermiso rp : authDao.findPermisosConPermiso(rolIds)) {
            authorities.add(new SimpleGrantedAuthority(rp.getPermiso().getCodigo()));
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(usuario.getActivo() == null || !usuario.getActivo())
                .build();
    }
}
