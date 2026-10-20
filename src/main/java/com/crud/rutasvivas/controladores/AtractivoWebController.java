
package com.crud.rutasvivas.controladores;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crud.rutasvivas.entidades.Atractivo;
import com.crud.rutasvivas.repositorios.ComentarioCalidadRepositorio;
import com.crud.rutasvivas.repositorios.AtractivoRepositorio;
import com.crud.rutasvivas.repositorios.RutaRepositorio;

@Controller
@RequestMapping("/atractivos")
public class AtractivoWebController {

    private final AtractivoRepositorio atractivoRepositorio;
    private final RutaRepositorio rutaRepositorio;
    private final ImagenControlador imagenControlador;
    private final ComentarioCalidadRepositorio comentarioRepositorio;

    public AtractivoWebController(
            AtractivoRepositorio atractivoRepositorio,
            RutaRepositorio rutaRepositorio,
            ImagenControlador imagenControlador,
            ComentarioCalidadRepositorio comentarioRepositorio) {

        this.atractivoRepositorio = atractivoRepositorio;
        this.rutaRepositorio = rutaRepositorio;
        this.imagenControlador = imagenControlador;
        this.comentarioRepositorio = comentarioRepositorio;
    }

    // Listar atractivos y aplicar filtros
    @GetMapping
    public String listar(
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String municipio,
            HttpSession sesion,
            Model modelo) {

        boolean admin = Boolean.TRUE.equals(sesion.getAttribute("admin"));

        List<Atractivo> atractivos;

        if (admin) {
            atractivos = atractivoRepositorio.findAll();
        } else {
            atractivos = atractivoRepositorio.findByPublicadoTrue();
        }

        if (departamento != null && !departamento.isBlank()) {
            String filtroDepartamento = departamento.trim();

            atractivos = atractivos.stream()
                    .filter(a -> a.getDepartamento() != null
                            && a.getDepartamento().equalsIgnoreCase(
                                    filtroDepartamento))
                    .toList();
        }

        if (municipio != null && !municipio.isBlank()) {
            String filtroMunicipio = municipio.trim();

            atractivos = atractivos.stream()
                    .filter(a -> a.getMunicipio() != null
                            && a.getMunicipio().equalsIgnoreCase(
                                    filtroMunicipio))
                    .toList();
        }

        atractivos.forEach(ImagenesCatalogo::completar);
        modelo.addAttribute("atractivos", atractivos);
        modelo.addAttribute("departamento", departamento);
        modelo.addAttribute("municipio", municipio);
        modelo.addAttribute("admin", admin);

        return "Atractivo/listar";
    }

    // Mostrar el detalle de un atractivo
    @GetMapping("/{id}")
    public String detalle(
            @PathVariable String id,
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        Atractivo atractivo = atractivoRepositorio.findById(id).orElse(null);

        boolean admin = Boolean.TRUE.equals(sesion.getAttribute("admin"));

        if (atractivo == null || (!admin && !atractivo.isPublicado())) {
            atributos.addFlashAttribute(
                    "error",
                    "El atractivo solicitado no está disponible.");

            return "redirect:/atractivos";
        }

        ImagenesCatalogo.completar(atractivo);
        modelo.addAttribute("atractivo", atractivo);
        modelo.addAttribute("comentariosCalidad",
                comentarioRepositorio.findByTipoContenidoAndContenidoIdAndPublicadoTrueOrderByFechaDesc("ATRACTIVO", id));
        modelo.addAttribute("admin", admin);

        return "Atractivo/detalle";
    }

    // Mostrar formulario para crear un atractivo
    @GetMapping("/nuevo")
    public String nuevo(
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute(
                    "error",
                    "Debes iniciar sesión como administrador.");

            return "redirect:/login";
        }

        modelo.addAttribute("atractivo", new Atractivo());

        return "Atractivo/formulario";
    }

    // Mostrar formulario para editar un atractivo existente
    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable String id,
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute(
                    "error",
                    "Debes iniciar sesión como administrador.");

            return "redirect:/login";
        }

        Atractivo atractivo = atractivoRepositorio.findById(id).orElse(null);

        if (atractivo == null) {
            atributos.addFlashAttribute(
                    "error",
                    "No se encontró el atractivo que deseas editar.");

            return "redirect:/atractivos";
        }

        modelo.addAttribute("atractivo", atractivo);

        return "Atractivo/formulario";
    }

    // Guardar un atractivo nuevo o actualizar uno existente
    @PostMapping("/guardar")
    public String guardar(
            @Valid Atractivo atractivo,
            BindingResult resultado,
            @RequestParam(name = "imagen", required = false)
            MultipartFile imagen,
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute(
                    "error",
                    "Debes iniciar sesión como administrador.");

            return "redirect:/login";
        }

        if (resultado.hasErrors()) {
            modelo.addAttribute("atractivo", atractivo);

            return "Atractivo/formulario";
        }

        // Comprobar el ID únicamente cuando se está editando.
        if (atractivo.getId() != null && !atractivo.getId().isBlank()) {

            Atractivo existente = atractivoRepositorio
                    .findById(atractivo.getId())
                    .orElse(null);

            if (existente == null) {
                atributos.addFlashAttribute(
                        "error",
                        "El atractivo que intentas editar ya no existe.");

                return "redirect:/atractivos";
            }

            // Si no se envía una imagen nueva, conservar la anterior.
            if ((imagen == null || imagen.isEmpty())
                    && (atractivo.getImagenUrl() == null || atractivo.getImagenUrl().isBlank())) {
                atractivo.setImagenUrl(existente.getImagenUrl());
            }
        }

        // Guardar la imagen si se seleccionó un archivo.
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String rutaImagen = imagenControlador.guardarImagen(imagen, "atractivos");
                atractivo.setImagenUrl(rutaImagen);

            } catch (Exception e) {
                modelo.addAttribute("atractivo", atractivo);
                modelo.addAttribute(
                        "errorImagen",
                        "No se pudo guardar la imagen. Selecciona otra e inténtalo de nuevo.");

                return "Atractivo/formulario";
            }
        }

        atractivoRepositorio.save(atractivo);

        atributos.addFlashAttribute(
                "mensaje",
                "El atractivo se guardó correctamente.");

        return "redirect:/atractivos";
    }

    // Eliminar un atractivo que no esté asociado a una ruta
    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable String id,
            HttpSession sesion,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute(
                    "error",
                    "Debes iniciar sesión como administrador.");

            return "redirect:/login";
        }

        if (!atractivoRepositorio.existsById(id)) {
            atributos.addFlashAttribute(
                    "error",
                    "El atractivo no existe.");

            return "redirect:/atractivos";
        }

        if (rutaRepositorio.existsByAtractivosIdsContaining(id)) {
            atributos.addFlashAttribute(
                    "error",
                    "No puedes eliminar este atractivo porque pertenece a una ruta.");

            return "redirect:/atractivos";
        }

        atractivoRepositorio.deleteById(id);

        atributos.addFlashAttribute(
                "mensaje",
                "El atractivo se eliminó correctamente.");

        return "redirect:/atractivos";
    }

    // Comprobar si hay una sesión de administrador
    private boolean esAdmin(HttpSession sesion) {
        return Boolean.TRUE.equals(sesion.getAttribute("admin"));
    }
}
