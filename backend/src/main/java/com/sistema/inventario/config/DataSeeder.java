package com.sistema.inventario.config;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeder academico: permisos, roles y usuarios base.
 *
 * <p>Es idempotente: se puede ejecutar en cada arranque sin duplicar nada y
 * sin perder datos ya sembrados. Solo crea lo que falta y migra el rol
 * viejo INVENTARIO al esquema de tres roles.
 *
 * <p>Las categorias NO se siembran aqui a proposito: las gestiona el usuario
 * desde la pantalla de productos ({@code /api/categorias}) y en la base ya
 * existen las del catalogo de componentes mas MONITOR y MOUSE.
 */
@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    /** Rol anterior al esquema de tres roles. Se migra y luego se elimina. */
    private static final String ROL_LEGADO = "INVENTARIO";

    /** Permisos del modelo, en el orden en que se muestran en el README. */
    private static final String[][] PERMISOS = {
            {"producto.crear", "Crear productos"},
            {"producto.ver", "Ver productos"},
            {"ingreso.registrar", "Registrar ingresos de mercaderia"},
            {"stock.ajustar", "Ajustar stock"},
            {"categoria.gestionar", "Gestionar categorias"},
            {"usuarios.ver", "Ver usuarios"},
    };

    /** ADMIN: todo. JEFE: almacen completo sin ver usuarios. AUXILIAR: ver + ingresos. */
    private static final List<String> PERMISOS_ADMIN = List.of(
            "producto.crear", "producto.ver", "ingreso.registrar",
            "stock.ajustar", "categoria.gestionar", "usuarios.ver");

    private static final List<String> PERMISOS_JEFE = List.of(
            "producto.crear", "producto.ver", "ingreso.registrar",
            "stock.ajustar", "categoria.gestionar");

    private static final List<String> PERMISOS_AUXILIAR = List.of(
            "producto.ver", "ingreso.registrar");

    @Bean
    @Transactional
    CommandLineRunner seed(RolRepository rolRepository,
                           PermisoRepository permisoRepository,
                           RolPermisoRepository rolPermisoRepository,
                           UsuarioRepository usuarioRepository,
                           UsuarioRolRepository usuarioRolRepository,
                           PasswordEncoder passwordEncoder) {
        return args -> {
            for (String[] p : PERMISOS) {
                permisoRepository.findByCodigo(p[0]).orElseGet(() -> {
                    Permiso n = new Permiso();
                    n.setCodigo(p[0]);
                    n.setDescripcion(p[1]);
                    return permisoRepository.save(n);
                });
            }

            Rol admin = getOrCreateRol(rolRepository, "ADMIN", "Administrador con acceso total");
            Rol jefe = getOrCreateRol(rolRepository, "JEFE_ALMACEN", "Jefe de almacen");
            Rol auxiliar = getOrCreateRol(rolRepository, "AUXILIAR_ALMACEN", "Auxiliar de almacen");

            asignarPermisos(rolPermisoRepository, admin, PERMISOS_ADMIN, permisoRepository);
            asignarPermisos(rolPermisoRepository, jefe, PERMISOS_JEFE, permisoRepository);
            asignarPermisos(rolPermisoRepository, auxiliar, PERMISOS_AUXILIAR, permisoRepository);

            crearUsuarioSiFalta(usuarioRepository, usuarioRolRepository, passwordEncoder,
                    "admin", "admin123", "Administrador", "admin@sistema.com", admin);
            crearUsuarioSiFalta(usuarioRepository, usuarioRolRepository, passwordEncoder,
                    "jefe", "jefe123", "Jefe de Almacen", "jefe@sistema.com", jefe);

            // El usuario "inventario" es el auxiliar. En una base ya sembrada por la
            // version anterior todavia apunta al rol INVENTARIO y hay que reasignarlo;
            // en una base nueva no existe y se crea aqui. Sin esto, una instalacion
            // limpia se quedaria sin el usuario auxiliar que anuncia el README.
            migrarUsuario(usuarioRepository, usuarioRolRepository, rolRepository, "inventario", auxiliar);
            crearUsuarioSiFalta(usuarioRepository, usuarioRolRepository, passwordEncoder,
                    "inventario", "inv123", "Usuario Inventario", "inventario@sistema.com", auxiliar);
            eliminarRolLegado(rolRepository, rolPermisoRepository, usuarioRolRepository);
            log.info("Roles listos: ADMIN, JEFE_ALMACEN, AUXILIAR_ALMACEN");
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

    private void asignarPermisos(RolPermisoRepository rolPermisoRepository,
                                 Rol rol,
                                 List<String> codigos,
                                 PermisoRepository permisoRepository) {
        for (String codigo : codigos) {
            permisoRepository.findByCodigo(codigo).ifPresent(permiso -> {
                if (!rolPermisoRepository.existsByRolIdAndPermisoId(rol.getId(), permiso.getId())) {
                    RolPermiso rp = new RolPermiso();
                    rp.setRol(rol);
                    rp.setPermiso(permiso);
                    rolPermisoRepository.save(rp);
                }
            });
        }
    }

    /**
     * Reasigna el rol del usuario si todavia apunta al rol legado. Cualquier otro
     * rol asignado se respeta: no se pisa la decision del administrador.
     * Si el usuario no existe todavia (base nueva), no hace nada: lo crea el
     * llamada siguiente a crearUsuarioSiFalta.
     */
    private void migrarUsuario(UsuarioRepository usuarioRepository,
                               UsuarioRolRepository usuarioRolRepository,
                               RolRepository rolRepository,
                               String username,
                               Rol destino) {
        usuarioRepository.findByUsername(username).ifPresent(usuario -> {
            List<UsuarioRol> asignaciones = usuarioRolRepository.findByUsuarioId(usuario.getId());
            boolean yaMigrado = asignaciones.stream()
                    .anyMatch(ur -> ur.getRol().getId().equals(destino.getId()));
            if (yaMigrado) {
                return;
            }
            Rol legado = rolLegado(rolRepository);
            for (UsuarioRol ur : asignaciones) {
                if (legado != null && ur.getRol().getId().equals(legado.getId())) {
                    ur.setRol(destino);
                    usuarioRolRepository.save(ur);
                    log.info("Usuario '{}' migrado de {} a {}",
                            username, legado.getNombre(), destino.getNombre());
                }
            }
        });
    }

    /** El rol INVENTARIO del esquema anterior, o null si ya no existe. */
    private Rol rolLegado(RolRepository rolRepository) {
        return rolRepository.findByNombre(ROL_LEGADO).orElse(null);
    }

    /**
     * Elimina el rol INVENTARIO del esquema anterior. El orden importa: primero
     * las filas hijas de rol_permiso y usuario_rol, porque ambas FK son
     * restrictivas (NO ACTION) y un DELETE directo seria rechazado.
     */
    private void eliminarRolLegado(RolRepository rolRepository,
                                   RolPermisoRepository rolPermisoRepository,
                                   UsuarioRolRepository usuarioRolRepository) {
        Rol legado = rolRepository.findByNombre(ROL_LEGADO).orElse(null);
        if (legado == null) {
            return;
        }
        rolPermisoRepository.deleteAll(rolPermisoRepository.findByRolId(legado.getId()));
        usuarioRolRepository.deleteAll(
                usuarioRolRepository.findAll().stream()
                        .filter(ur -> ur.getRol().getId().equals(legado.getId()))
                        .toList());
        rolRepository.delete(legado);
        log.info("Rol legado {} eliminado tras migrar sus usuarios", ROL_LEGADO);
    }

    private void crearUsuarioSiFalta(UsuarioRepository usuarioRepository,
                                     UsuarioRolRepository usuarioRolRepository,
                                     PasswordEncoder passwordEncoder,
                                     String username, String rawPassword,
                                     String nombreCompleto, String email, Rol rol) {
        if (usuarioRepository.existsByUsername(username)) {
            return;
        }
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
