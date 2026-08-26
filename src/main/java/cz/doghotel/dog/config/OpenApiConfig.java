package cz.doghotel.dog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger UI (springdoc) — deklaruje bearer JWT schéma. Zdroj pravdy je spec v bc-dh-dog-api. */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI dogServiceOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Dog Hotel — Dog service")
                .description("Běžící implementace kontraktu bc-dh-dog-api")
                .version("0.1.0"))
            .components(new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
