package com.meucampus.matriculas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI matriculasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MeuCampus - Serviço de Matrículas")
                        .description("API de matrículas integrada aos serviços de Cadastro e Turmas.")
                        .version("2.0"));
    }
}
