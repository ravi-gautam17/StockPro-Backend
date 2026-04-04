package com.stockpro.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 for Swagger UI. <b>Do not</b> set a global {@code security} requirement here — that would
 * mark {@code /auth/login} as needing a JWT. Secured routes use {@code @SecurityRequirement(name = "bearer")}
 * per controller (or method).
 * <p>
 * <b>Try it:</b> open {@code /swagger-ui/index.html} → {@code POST /api/v1/auth/login} → copy {@code token} →
 * <b>Authorize</b> → HTTP bearer → paste (Swagger sends {@code Authorization: Bearer …}).
 */
@Configuration
public class OpenApiConfig {

    /** Must match {@code @SecurityRequirement(name = "bearer")} on REST controllers. */
    public static final String SCHEME_NAME = "bearer";

    @Bean
    public OpenAPI stockProOpenApi() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/").description("Backend (port 8080 by default)")))
                .info(new Info()
                        .title("StockPro Monolith API")
                        .version("1.0")
                        .description("""
                                Monolithic inventory API (/api/v1/**). Default admin (seeded on first run):
                                {@code admin@stockpro.local} / {@code Admin@123}.

                                **Swagger:** Call **Authorize** and choose **bearer**; paste the JWT from login (no `Bearer ` prefix).
                                """))
                .components(new Components().addSecuritySchemes(SCHEME_NAME,
                        new SecurityScheme()
                                .name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT string returned by POST /api/v1/auth/login in field `token`.")));
    }
}
