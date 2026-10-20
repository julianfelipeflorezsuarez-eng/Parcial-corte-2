
package com.crud.rutasvivas.controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crud.rutasvivas.entidades.Atractivo;
import com.crud.rutasvivas.entidades.Ruta;
import com.crud.rutasvivas.repositorios.ComentarioCalidadRepositorio;
import com.crud.rutasvivas.repositorios.AtractivoRepositorio;
import com.crud.rutasvivas.repositorios.RutaRepositorio;

@Controller
@RequestMapping("/rutas")
public class RutaWebController {

    private final RutaRepositorio rutaRepositorio;
    private final AtractivoRepositorio atractivoRepositorio;
    private final ComentarioCalidadRepositorio comentarioRepositorio;

    public RutaWebController(
            RutaRepositorio rutaRepositorio,
            AtractivoRepositorio atractivoRepositorio,
            ComentarioCalidadRepositorio comentarioRepositorio) {
        this.rutaRepositorio = rutaRepositorio;
        this.atractivoRepositorio = atractivoRepositorio;
        this.comentarioRepositorio = comentarioRepositorio;
    }

    @GetMapping
    public String listar(HttpSession sesion, Model modelo) {
        boolean admin = esAdmin(sesion);

        List<Ruta> rutas = rutaRepositorio.findAll();
        List<Atractivo> atractivos = admin
                ? atractivoRepositorio.findAll()
                : atractivoRepositorio.findByPublicadoTrue();

        modelo.addAttribute("rutas", rutas);
        Map<String, String> imagenesRutas = new HashMap<>();
        for (Ruta ruta : rutas) {
            if (ruta.getAtractivosIds() != null) {
                for (String atractivoId : ruta.getAtractivosIds()) {
                    Atractivo parada = atractivoRepositorio.findById(atractivoId).orElse(null);
                    if (parada != null) ImagenesCatalogo.completar(parada);
                    if (parada != null && parada.getImagenUrl() != null && !parada.getImagenUrl().isBlank()) {
                        imagenesRutas.put(ruta.getId(), parada.getImagenUrl());
                        break;
                    }
                }
            }
        }
        modelo.addAttribute("imagenesRutas", imagenesRutas);
        modelo.addAttribute("atractivos", atractivos);
        modelo.addAttribute("admin", admin);

        return "Ruta/listar";
    }

    @GetMapping("/{id}")
    public String detalle(
            @PathVariable String id,
            Model modelo,
            RedirectAttributes atributos) {

        Ruta ruta = rutaRepositorio.findById(id).orElse(null);

        if (ruta == null) {
            atributos.addFlashAttribute("error", "La ruta solicitada no existe.");
            return "redirect:/rutas";
        }

        List<Atractivo> paradas = new ArrayList<>();

        if (ruta.getAtractivosIds() != null) {
            for (String atractivoId : ruta.getAtractivosIds()) {
                atractivoRepositorio.findById(atractivoId).ifPresent(paradas::add);
            }
        }

        paradas.forEach(ImagenesCatalogo::completar);
        modelo.addAttribute("ruta", ruta);
        modelo.addAttribute("paradas", paradas);
        modelo.addAttribute("comentariosCalidad",
                comentarioRepositorio.findByTipoContenidoAndContenidoIdAndPublicadoTrueOrderByFechaDesc("RUTA", id));
        return "Ruta/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute("error", "Solo el administrador puede crear rutas.");
            return "redirect:/login";
        }

        modelo.addAttribute("ruta", new Ruta());
        modelo.addAttribute("atractivos", atractivoRepositorio.findAll());
        return "Ruta/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable String id,
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute("error", "Solo el administrador puede editar rutas.");
            return "redirect:/login";
        }

        Ruta ruta = rutaRepositorio.findById(id).orElse(null);

        if (ruta == null) {
            atributos.addFlashAttribute("error", "No se encontró la ruta.");
            return "redirect:/rutas";
        }

        modelo.addAttribute("ruta", ruta);
        modelo.addAttribute("atractivos", atractivoRepositorio.findAll());
        return "Ruta/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Ruta ruta,
            BindingResult resultado,
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute("error", "Solo el administrador puede guardar rutas.");
            return "redirect:/login";
        }

        if (resultado.hasErrors()) {
            modelo.addAttribute("ruta", ruta);
            modelo.addAttribute("atractivos", atractivoRepositorio.findAll());
            return "Ruta/formulario";
        }

        if (ruta.getId() != null && !ruta.getId().isBlank()
                && !rutaRepositorio.existsById(ruta.getId())) {
            atributos.addFlashAttribute("error", "La ruta que intentas editar ya no existe.");
            return "redirect:/rutas";
        }

        if (ruta.getAtractivosIds() != null) {
            for (String idAtractivo : ruta.getAtractivosIds()) {
                if (idAtractivo == null || !atractivoRepositorio.existsById(idAtractivo)) {
                    atributos.addFlashAttribute(
                            "error", "La ruta contiene un atractivo que no existe.");
                    return "redirect:/rutas";
                }
            }
        }

        rutaRepositorio.save(ruta);
        atributos.addFlashAttribute("mensaje", "La ruta se guardó correctamente.");
        return "redirect:/rutas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable String id,
            HttpSession sesion,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute("error", "Solo el administrador puede eliminar rutas.");
            return "redirect:/login";
        }

        if (!rutaRepositorio.existsById(id)) {
            atributos.addFlashAttribute("error", "La ruta no existe.");
            return "redirect:/rutas";
        }

        rutaRepositorio.deleteById(id);
        atributos.addFlashAttribute("mensaje", "La ruta se eliminó correctamente.");
        return "redirect:/rutas";
    }

    private boolean esAdmin(HttpSession sesion) {
        return Boolean.TRUE.equals(sesion.getAttribute("admin"));
    }
}
