package com.sistema.inventario.config;

import com.sistema.inventario.auth.entity.*;
import com.sistema.inventario.auth.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import com.sistema.inventario.auth.entity.Permiso;
import com.sistema.inventario.auth.entity.Rol;
import com.sistema.inventario.auth.entity.RolPermiso;
import com.sistema.inventario.auth.entity.Usuario;
import com.sistema.inventario.auth.entity.UsuarioRol;
import com.sistema.inventario.auth.repository.PermisoRepository;
import com.sistema.inventario.auth.repository.RolPermisoRepository;
import com.sistema.inventario.auth.repository.RolRepository;
import com.sistema.inventario.auth.repository.UsuarioRepository;
import com.sistema.inventario.auth.repository.UsuarioRolRepository;
import com.sistema.inventario.catalogo.categoria.entity.Categoria;
import com.sistema.inventario.catalogo.categoria.repository.CategoriaRepository;
import com.sistema.inventario.common.TipoComponente;

/**
 * Seeder academico: roles, permisos, usuarios y categorias base.
 * Reemplaza al antiguo GET /api/auth/init.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(RolRepository rolRepository,
                           PermisoRepository permisoRepository,
                           RolPermisoRepository rolPermisoRepository,
                           UsuarioRepository usuarioRepository,
                           UsuarioRolRepository usuarioRolRepository,
                           CategoriaRepository categoriaRepository,
                           PasswordEncoder passwordEncoder) {
        return args -> {
            Rol admin = getOrCreateRol(rolRepository, "ADMIN", "Administrador con acceso total");
            Rol inventario = getOrCreateRol(rolRepository, "INVENTARIO", "Acceso a inventario");

            List<String[]> permisos = List.of(
                    new String[]{"producto.crear", "Crear productos"},
                    new String[]{"producto.ver", "Ver productos"},
                    new String[]{"ingreso.registrar", "Registrar ingresos de mercaderia"},
                    new String[]{"stock.ajustar", "Ajustar stock"},
                    new String[]{"categoria.gestionar", "Gestionar categorias"},
                    new String[]{"usuarios.ver", "Ver usuarios"}
            );
            for (String[] p : permisos) {
                Permiso permiso = permisoRepository.findByCodigo(p[0]).orElseGet(() -> {
                    Permiso n = new Permiso();
                    n.setCodigo(p[0]);
                    n.setDescripcion(p[1]);
                    return permisoRepository.save(n);
                });
                if (!rolPermisoRepository.existsByRolIdAndPermisoId(admin.getId(), permiso.getId())) {
                    RolPermiso rp = new RolPermiso();
                    rp.setRol(admin);
                    rp.setPermiso(permiso);
                    rolPermisoRepository.save(rp);
                }
            }
            // rol INVENTARIO: subconjunto
            for (String codigo : List.of("producto.crear", "producto.ver", "ingreso.registrar", "stock.ajustar")) {
                permisoRepository.findByCodigo(codigo).ifPresent(permiso -> {
                    if (!rolPermisoRepository.existsByRolIdAndPermisoId(inventario.getId(), permiso.getId())) {
                        RolPermiso rp = new RolPermiso();
                        rp.setRol(inventario);
                        rp.setPermiso(permiso);
                        rolPermisoRepository.save(rp);
                    }
                });
            }

            createUserIfMissing(usuarioRepository, usuarioRolRepository, passwordEncoder,
                    "admin", "admin123", "Administrador", "admin@sistema.com", admin);
            createUserIfMissing(usuarioRepository, usuarioRolRepository, passwordEncoder,
                    "inventario", "inv123", "Usuario Inventario", "inventario@sistema.com", inventario);

            // categorias base para TipoComponente (Opcion A)
            for (String cat : List.of("CPU", "GPU", "RAM", "SSD", "HDD", "PLACA_MADRE", "FUENTE", "GABINETE")) {
                if (categoriaRepository.findByNombre(cat).isEmpty()) {
                    Categoria c = new Categoria();
                    c.setNombre(cat);
                    c.setDescripcion("Categoria de componente " + cat);
                    categoriaRepository.save(c);
                }
            }
        };
    }

    private Rol getOrCreateRol(RolRepository repo, String nombre, String descripcion) {
        return repo.findByNombre(nombre).orElseGet(() -> {
            Rol r = new Rol();
            r.setNombre(nombre);
            r.setDescripcion(descripcion);
            return repo.save(r);
        });
    }

    private void createUserIfMissing(UsuarioRepository usuarioRepository,
                                     UsuarioRolRepository usuarioRolRepository,
                                     PasswordEncoder passwordEncoder,
                                     String username, String rawPassword,
                                     String nombreCompleto, String email, Rol rol) {
        if (!usuarioRepository.existsByUsername(username)) {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(rawPassword));
            u.setNombreCompleto(nombreCompleto);
            u.setEmail(email);
            u.setActivo(true);
            u = usuarioRepository.save(u);

            UsuarioRol ur = new UsuarioRol();
            ur.setUsuario(u);
            ur.setRol(rol);
            usuarioRolRepository.save(ur);
        }
    }
}
