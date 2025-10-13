package com.example.honour_U_Springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ruta absoluta a la carpeta "uploads" dentro del proyecto
        String uploadPath = Paths.get(System.getProperty("user.dir"), "uploads").toUri().toString();

        // Mapea cualquier URL que empiece con /uploads/ para servir esos archivos directamente
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
