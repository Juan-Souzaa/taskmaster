package com.taskmaster.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TASKMASTER API")
                        .description("API REST para gerenciamento de tarefas pessoais")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe TASKMASTER")
                                .email("contato@taskmaster.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
