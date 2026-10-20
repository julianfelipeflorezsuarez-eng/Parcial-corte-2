
package com.crud.rutasvivas.controladores;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.crud.rutasvivas.repositorios.AtractivoRepositorio;
import com.crud.rutasvivas.repositorios.ExperienciaRepositorio;
import com.crud.rutasvivas.repositorios.RutaRepositorio;

@Controller
public class InicioControlador {

    private final AtractivoRepositorio atractivoRepositorio;
    private final RutaRepositorio rutaRepositorio;
    private final ExperienciaRepositorio experienciaRepositorio;

    public InicioControlador(
            AtractivoRepositorio atractivoRepositorio,
            RutaRepositorio rutaRepositorio,
            ExperienciaRepositorio experienciaRepositorio) {
        this.atractivoRepositorio = atractivoRepositorio;
        this.rutaRepositorio = rutaRepositorio;
        this.experienciaRepositorio = experienciaRepositorio;
    }

    @GetMapping("/")
    public String inicio(Model modelo, HttpSession sesion) {
        var atractivos = atractivoRepositorio.findByPublicadoTrue()
                .stream().limit(6).toList();
        atractivos.forEach(ImagenesCatalogo::completar);
        modelo.addAttribute("atractivos", atractivos);
        var experiencias = experienciaRepositorio.findAll().stream().limit(3).toList();
        experiencias.forEach(ImagenesCatalogo::completar);
        modelo.addAttribute("experiencias", experiencias);
        modelo.addAttribute("rutas",
                rutaRepositorio.findAll().stream().limit(3).toList());
        modelo.addAttribute("usuarioNombre", sesion.getAttribute("usuarioNombre"));
        modelo.addAttribute("usuarioRol", sesion.getAttribute("usuarioRol"));
        modelo.addAttribute("admin",
                Boolean.TRUE.equals(sesion.getAttribute("admin")));

        return "index";
    }
}
