package com.spring.app.proyecto_test.springboot_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;



@Configuration
public class SpringFoxConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("API de Cuentas")
                    .version("1.0")
                    .description("Documentación de la API de Cuentas con OpenAPI y Springdoc"));
    }





}
