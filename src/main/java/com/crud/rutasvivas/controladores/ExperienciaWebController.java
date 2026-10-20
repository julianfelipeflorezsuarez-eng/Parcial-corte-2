
package com.crud.rutasvivas.controladores;

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

import com.crud.rutasvivas.entidades.Experiencia;
import com.crud.rutasvivas.entidades.ComentarioCalidad;
import com.crud.rutasvivas.repositorios.ComentarioCalidadRepositorio;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.crud.rutasvivas.repositorios.ExperienciaRepositorio;
import com.crud.rutasvivas.repositorios.RutaRepositorio;

@Controller
@RequestMapping("/experiencias")
public class ExperienciaWebController {

    private final ExperienciaRepositorio experienciaRepositorio;
    private final ComentarioCalidadRepositorio comentarioRepositorio;
    private final RutaRepositorio rutaRepositorio;
    private final ImagenControlador imagenControlador;

    public ExperienciaWebController(
            ExperienciaRepositorio experienciaRepositorio,
            ComentarioCalidadRepositorio comentarioRepositorio,
            RutaRepositorio rutaRepositorio,
            ImagenControlador imagenControlador) {
        this.experienciaRepositorio = experienciaRepositorio;
        this.comentarioRepositorio = comentarioRepositorio;
        this.rutaRepositorio = rutaRepositorio;
        this.imagenControlador = imagenControlador;
    }

    @GetMapping
    public String listar(Model modelo, HttpSession sesion) {
        var experiencias = experienciaRepositorio.findAll();
        experiencias.forEach(ImagenesCatalogo::completar);
        modelo.addAttribute("experiencias", experiencias);
        modelo.addAttribute("admin",
                Boolean.TRUE.equals(sesion.getAttribute("admin")));
        modelo.addAttribute("puedeGestionar",
                Boolean.TRUE.equals(sesion.getAttribute("admin"))
                || "GESTOR_CULTURAL".equals(sesion.getAttribute("usuarioRol")));
        return "Experiencia/listar";
    }

    @GetMapping("/{id}")
    public String detalle(
            @PathVariable String id,
            Model modelo,
            RedirectAttributes atributos) {

        Experiencia experiencia = experienciaRepositorio.findById(id).orElse(null);

        if (experiencia == null) {
            atributos.addFlashAttribute("error", "La experiencia solicitada no existe.");
            return "redirect:/experiencias";
        }

        ImagenesCatalogo.completar(experiencia);
        modelo.addAttribute("experiencia", experiencia);
        modelo.addAttribute("comentarios", comentarioRepositorio.findByTipoContenidoAndContenidoIdAndPublicadoTrueOrderByFechaDesc("EXPERIENCIA", id));
        return "Experiencia/detalle";
    }

    @PostMapping("/{id}/comentarios")
    public String comentar(@PathVariable String id, @RequestParam String autor, @RequestParam String comentario,
            RedirectAttributes atributos) {
        Experiencia experiencia = experienciaRepositorio.findById(id).orElse(null);
        if (experiencia == null) {
            atributos.addFlashAttribute("error", "La experiencia ya no existe.");
            return "redirect:/experiencias";
        }
        if (autor == null || autor.isBlank() || autor.trim().length() > 80 || comentario == null || comentario.isBlank() || comentario.trim().length() > 1500) {
            atributos.addFlashAttribute("error", "Escribe tu nombre y un comentario de hasta 1500 caracteres.");
            return "redirect:/experiencias/" + id;
        }
        ComentarioCalidad c = new ComentarioCalidad();
        c.setTipoContenido("EXPERIENCIA"); c.setContenidoId(id); c.setNombreContenido(experiencia.getNombre());
        c.setComentario(comentario.trim()); c.setAutor(autor.trim()); c.setEstado("PENDIENTE"); c.setPublicado(false);
        comentarioRepositorio.save(c);
        atributos.addFlashAttribute("mensaje", "Gracias. Tu comentario será visible cuando el auditor lo apruebe.");
        return "redirect:/experiencias/" + id;
    }

    @GetMapping("/nuevo")
    public String nuevo(
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!puedeGestionarExperiencias(sesion)) {
            atributos.addFlashAttribute("error", "Debes iniciar sesión como administrador o gestor cultural.");
            return "redirect:/login";
        }

        modelo.addAttribute("experiencia", new Experiencia());
        modelo.addAttribute("rutas", rutaRepositorio.findAll());
        return "Experiencia/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable String id,
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!puedeGestionarExperiencias(sesion)) {
            atributos.addFlashAttribute("error", "Debes iniciar sesión como administrador o gestor cultural.");
            return "redirect:/login";
        }

        Experiencia experiencia = experienciaRepositorio.findById(id).orElse(null);

        if (experiencia == null) {
            atributos.addFlashAttribute("error", "No se encontró la experiencia.");
            return "redirect:/experiencias";
        }

        modelo.addAttribute("experiencia", experiencia);
        modelo.addAttribute("rutas", rutaRepositorio.findAll());
        return "Experiencia/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Experiencia experiencia,
            BindingResult resultado,
            @RequestParam(name = "imagen", required = false) MultipartFile imagen,
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!puedeGestionarExperiencias(sesion)) {
            atributos.addFlashAttribute("error", "Debes iniciar sesión como administrador o gestor cultural.");
            return "redirect:/login";
        }

        if (resultado.hasErrors()) {
            modelo.addAttribute("experiencia", experiencia);
            modelo.addAttribute("rutas", rutaRepositorio.findAll());
            return "Experiencia/formulario";
        }

        Experiencia existente = null;
        if (experiencia.getId() != null && !experiencia.getId().isBlank()) {
            existente = experienciaRepositorio.findById(experiencia.getId()).orElse(null);
            if (existente == null) {
                atributos.addFlashAttribute("error", "La experiencia que intentas editar ya no existe.");
                return "redirect:/experiencias";
            }
            if ((imagen == null || imagen.isEmpty())
                    && (experiencia.getImagenUrl() == null || experiencia.getImagenUrl().isBlank())) {
                experiencia.setImagenUrl(existente.getImagenUrl());
            }
        }

        if (imagen != null && !imagen.isEmpty()) {
            try {
                experiencia.setImagenUrl(imagenControlador.guardarImagen(imagen, "experiencias"));
            } catch (Exception e) {
                modelo.addAttribute("experiencia", experiencia);
                modelo.addAttribute("rutas", rutaRepositorio.findAll());
                modelo.addAttribute("errorImagen", e.getMessage());
                return "Experiencia/formulario";
            }
        }

        if (experiencia.getRutaId() != null && !experiencia.getRutaId().isBlank()
                && !rutaRepositorio.existsById(experiencia.getRutaId())) {
            atributos.addFlashAttribute("error", "La ruta relacionada no existe.");
            return "redirect:/experiencias";
        }
        experienciaRepositorio.save(experiencia);
        atributos.addFlashAttribute("mensaje", "La experiencia se guardó correctamente.");
        return "redirect:/experiencias";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable String id,
            HttpSession sesion,
            RedirectAttributes atributos) {

        if (!puedeGestionarExperiencias(sesion)) {
            atributos.addFlashAttribute("error", "Debes iniciar sesión como administrador o gestor cultural.");
            return "redirect:/login";
        }

        if (!experienciaRepositorio.existsById(id)) {
            atributos.addFlashAttribute("error", "La experiencia no existe.");
            return "redirect:/experiencias";
        }

        experienciaRepositorio.deleteById(id);
        atributos.addFlashAttribute("mensaje", "La experiencia se eliminó correctamente.");
        return "redirect:/experiencias";
    }

    private boolean puedeGestionarExperiencias(HttpSession sesion) {
        return Boolean.TRUE.equals(sesion.getAttribute("admin"))
                || "GESTOR_CULTURAL".equals(sesion.getAttribute("usuarioRol"));
    }
}
