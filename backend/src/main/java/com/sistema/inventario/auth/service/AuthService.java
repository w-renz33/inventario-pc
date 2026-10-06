package com.sistema.inventario.auth.service;

import com.sistema.inventario.auth.dto.LoginRequest;
import com.sistema.inventario.auth.dto.LoginResponse;
import com.sistema.inventario.auth.entity.Usuario;
import com.sistema.inventario.auth.entity.UsuarioRol;
import com.sistema.inventario.auth.repository.AuthDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final AuthDao authDao;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthService(AuthDao authDao,
                       AuthenticationManager authenticationManager) {
        this.authDao = authDao;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        // Login programatico: hay que persistir el contexto en la sesion explicitamente
        // (Spring Security 6 no lo guarda solo al autenticar fuera de un filtro).
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        return resumen(request.getUsername());
    }

    public LoginResponse me(String username) {
        return resumen(username);
    }

    private LoginResponse resumen(String username) {
        Usuario usuario = authDao.findUsuarioByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<UsuarioRol> urs = authDao.findRolesConRol(usuario.getId());
        List<String> roles = urs.stream().map(ur -> ur.getRol().getNombre()).distinct().toList();
        List<Long> rolIds = urs.stream().map(ur -> ur.getRol().getId()).distinct().toList();
        List<String> permisos = authDao.findPermisosConPermiso(rolIds).stream()
                .map(rp -> rp.getPermiso().getCodigo())
                .distinct()
                .toList();

        return new LoginResponse(usuario.getUsername(), roles, permisos);
    }
}
