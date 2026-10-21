package com.sistema.inventario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configuración de OpenAPI en /swagger-ui.html.
// La autenticación es por sesión (JSESSIONID HttpOnly), no por token, así que
// no se declara un esquema de seguridad. Para probar, iniciar sesión con
// POST /api/auth/login y conservar la cookie de sesión.
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Inventario API")
                        .version("1.0")
                        .description("""
                                API de inventario de componentes PC con autenticacion por sesion.

                                Autenticacion: POST /api/auth/login devuelve una sesion (cookie JSESSIONID).
                                El cliente debe reenviar esa cookie en cada peticion posterior; el frontend
                                lo hace con withCredentials. GET /api/auth/me confirma la sesion.

                                Autorizacion por rol: ADMIN, JEFE_ALMACEN y AUXILIAR_ALMACEN. Cada rol tiene
                                un subconjunto de permisos recurso.accion; el detalle esta en
                                /api/auth/me (campo permisos).""")
                        .contact(new Contact()
                                .name("Soporte")
                                .email("soporte@inventario.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
