package com.crud.rutasvivas;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracionWeb implements WebMvcConfigurer {

    private final ControlAccesoInterceptor controlAccesoInterceptor;

    public ConfiguracionWeb(
            ControlAccesoInterceptor controlAccesoInterceptor) {
        this.controlAccesoInterceptor = controlAccesoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(controlAccesoInterceptor);
    }
}