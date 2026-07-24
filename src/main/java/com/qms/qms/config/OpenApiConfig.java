package com.qms.qms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI qmsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("QMS Backend API")
                        .description("Garment QA Checking (QMS) - REST API")
                        .version("v1"));
    }
}
