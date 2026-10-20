package com.crud.rutasvivas.controladores;

import com.crud.rutasvivas.repositorios.AtractivoRepositorio;
import com.crud.rutasvivas.repositorios.ExperienciaRepositorio;
import com.crud.rutasvivas.repositorios.RutaRepositorio;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiRestControlador {

    private final AtractivoRepositorio atractivoRepositorio;
    private final RutaRepositorio rutaRepositorio;
    private final ExperienciaRepositorio experienciaRepositorio;

    public ApiRestControlador(AtractivoRepositorio atractivoRepositorio,
                              RutaRepositorio rutaRepositorio,
                              ExperienciaRepositorio experienciaRepositorio) {
        this.atractivoRepositorio = atractivoRepositorio;
        this.rutaRepositorio = rutaRepositorio;
        this.experienciaRepositorio = experienciaRepositorio;
    }

    @GetMapping
    public Map<String, Object> estado() {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("aplicacion", "RUTASVIVASCOLOMBIA");
        respuesta.put("estado", "activa");
        respuesta.put("atractivos", atractivoRepositorio.count());
        respuesta.put("rutas", rutaRepositorio.count());
        respuesta.put("experiencias", experienciaRepositorio.count());
        return respuesta;
    }
}