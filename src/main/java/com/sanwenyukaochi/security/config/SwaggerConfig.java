package com.sanwenyukaochi.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "staging"})
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer Token");

        SecurityRequirement bearerRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot Security API")
                        .version("1.0")
                        .description("这是一个用于 Spring Security 的 Spring Boot 项目")
                        .license(new License()
                                .name("我们的许可证链接")
                                .url("https://sanwenyukaochi.com"))
                        .contact(new Contact()
                                .name("YiFan Song")
                                .email("sanwenyukaochi@outlook.com")
                                .url("https://github.com/sanwenyukaochi")))
                .externalDocs(new ExternalDocumentation()
                        .description("项目文档")
                        .url("https://sanwenyukaochi.com"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", bearerScheme))
                .addSecurityItem(bearerRequirement);
    }
}
