package com.odissey.tour.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Tour-Odissey",
                        url = "http://www.tour-odissey.com"
                ),
                description = "OpenApi documentation for Tour Odissey Management",
                title = "OpenApi specification - Tour Odissey",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "LOCALHOST ENV",
                        url = "http://localhost:${server.port}${server.servlet.context-path}"
                ),
                @Server(
                        description = "Tour Odissey STAGING",
                        url = "http://test.tour-odissey.com:8081${server.servlet.context-path}"
                ),
                @Server(
                        description = "Tour Odissey PROD",
                        url = "http://www.tour-odissey,com:8081${server.servlet.context-path}"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT for Tour Odissey Application",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}