
package com.crud.rutasvivas.controladores;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.text.Normalizer;
import com.crud.rutasvivas.entidades.Experiencia;
import com.crud.rutasvivas.entidades.Ruta;
import com.crud.rutasvivas.entidades.Solicitud;
import com.crud.rutasvivas.entidades.ComentarioCalidad;
import com.crud.rutasvivas.repositorios.ComentarioCalidadRepositorio;
import com.crud.rutasvivas.repositorios.AtractivoRepositorio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crud.rutasvivas.repositorios.ExperienciaRepositorio;
import com.crud.rutasvivas.repositorios.RutaRepositorio;
import com.crud.rutasvivas.repositorios.SolicitudRepositorio;

@Controller
public class PerfilesControlador {

    private final RutaRepositorio rutaRepositorio;
    private final ExperienciaRepositorio experienciaRepositorio;
    private final SolicitudRepositorio solicitudRepositorio;
    private final ComentarioCalidadRepositorio comentarioRepositorio;
    private final AtractivoRepositorio atractivoRepositorio;

    public PerfilesControlador(
            RutaRepositorio rutaRepositorio,
            ExperienciaRepositorio experienciaRepositorio,
            SolicitudRepositorio solicitudRepositorio,
            ComentarioCalidadRepositorio comentarioRepositorio,
            AtractivoRepositorio atractivoRepositorio) {
        this.rutaRepositorio = rutaRepositorio;
        this.experienciaRepositorio = experienciaRepositorio;
        this.solicitudRepositorio = solicitudRepositorio;
        this.comentarioRepositorio = comentarioRepositorio;
        this.atractivoRepositorio = atractivoRepositorio;
    }

    @GetMapping("/guia")
    public String guia(
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!tieneRol(sesion, "GUIA")) {
            atributos.addFlashAttribute("error", "Esta sección es solo para usuarios con perfil de guía.");
            return "redirect:/login";
        }

        String nombreGuia = String.valueOf(sesion.getAttribute("usuarioNombre"));
        String correoGuia = String.valueOf(sesion.getAttribute("usuarioCorreo"));
        List<Ruta> rutasPropias = rutaRepositorio.findAll().stream()
                .filter(r -> coincideResponsable(r.getGuia(), nombreGuia, correoGuia) || esRutaGeneral(r.getGuia()))
                .toList();
        Set<String> idsRutas = rutasPropias.stream().map(Ruta::getId).collect(Collectors.toSet());
        List<Experiencia> experienciasDeRutas = experienciaRepositorio.findAll().stream()
                .filter(e -> e.getRutaId() != null && idsRutas.contains(e.getRutaId())).toList();
        Set<String> idsExperiencias = experienciasDeRutas.stream().map(Experiencia::getId).collect(Collectors.toSet());
        List<Solicitud> solicitudesPropias = solicitudRepositorio.findAllByOrderByFechaCreacionDesc().stream()
                .filter(s -> (s.getRutaId() != null && idsRutas.contains(s.getRutaId()))
                        || (s.getExperienciaId() != null && idsExperiencias.contains(s.getExperienciaId())))
                .toList();
        modelo.addAttribute("rutas", rutasPropias);
        modelo.addAttribute("solicitudes", solicitudesPropias);
        modelo.addAttribute("experiencias", experienciasDeRutas);

        return "Perfiles/guia";
    }

    @GetMapping("/gestor-cultural")
    public String gestor(
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!tieneRol(sesion, "GESTOR_CULTURAL")) {
            atributos.addFlashAttribute(
                    "error", "Esta sección es solo para gestores culturales.");
            return "redirect:/login";
        }

        List<Solicitud> solicitudesExperiencias = solicitudRepositorio.findAllByOrderByFechaCreacionDesc().stream()
                .filter(s -> "EXPERIENCIA".equalsIgnoreCase(s.getTipoSolicitud())
                        || (s.getExperienciaId() != null && !s.getExperienciaId().isBlank()))
                .toList();
        modelo.addAttribute("experiencias", experienciaRepositorio.findAll());
        modelo.addAttribute("solicitudes", solicitudesExperiencias);
        modelo.addAttribute("totalExperiencias", experienciaRepositorio.count());
        modelo.addAttribute("totalRutas", rutaRepositorio.count());
        return "Perfiles/gestor-cultural";
    }

    @GetMapping("/auditor")
    public String auditor(
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!tieneRol(sesion, "AUDITOR")) {
            atributos.addFlashAttribute(
                    "error", "Esta sección es solo para usuarios con perfil de auditor.");
            return "redirect:/login";
        }

        modelo.addAttribute("totalRutas", rutaRepositorio.count());
        modelo.addAttribute("totalExperiencias", experienciaRepositorio.count());
        modelo.addAttribute("totalSolicitudes", solicitudRepositorio.count());
        modelo.addAttribute("atractivos", atractivoRepositorio.findAll());
        modelo.addAttribute("rutas", rutaRepositorio.findAll());
        modelo.addAttribute("experiencias", experienciaRepositorio.findAll());
        modelo.addAttribute("comentarios", comentarioRepositorio.findAllByOrderByFechaDesc());
        modelo.addAttribute("solicitudes",
                solicitudRepositorio.findAllByOrderByFechaCreacionDesc().stream().limit(8).toList());

        return "Perfiles/auditor";
    }


    @org.springframework.web.bind.annotation.PostMapping("/auditor/comentarios")
    public String guardarComentario(@org.springframework.web.bind.annotation.RequestParam String objetivo,
            @org.springframework.web.bind.annotation.RequestParam String comentario, HttpSession sesion,
            RedirectAttributes atributos) {
        if (!tieneRol(sesion, "AUDITOR")) { atributos.addFlashAttribute("error", "Acceso no autorizado."); return "redirect:/login"; }
        if (comentario == null || comentario.isBlank() || comentario.trim().length() > 1500) {
            atributos.addFlashAttribute("error", "Escribe un comentario de calidad (máximo 1500 caracteres)."); return "redirect:/auditor";
        }
        String[] partes = objetivo.split("\\|", 2);
        if (partes.length != 2) { atributos.addFlashAttribute("error", "Selecciona un contenido válido."); return "redirect:/auditor"; }
        String tipo = partes[0], id = partes[1], nombre = null;
        if ("ATRACTIVO".equals(tipo)) nombre = atractivoRepositorio.findById(id).map(a -> a.getNombre()).orElse(null);
        else if ("RUTA".equals(tipo)) nombre = rutaRepositorio.findById(id).map(r -> r.getNombre()).orElse(null);
        else if ("EXPERIENCIA".equals(tipo)) nombre = experienciaRepositorio.findById(id).map(e -> e.getNombre()).orElse(null);
        if (nombre == null) { atributos.addFlashAttribute("error", "El contenido seleccionado ya no existe."); return "redirect:/auditor"; }
        ComentarioCalidad registro = new ComentarioCalidad(); registro.setTipoContenido(tipo); registro.setContenidoId(id);
        registro.setNombreContenido(nombre); registro.setComentario(comentario.trim());
        registro.setAutor(String.valueOf(sesion.getAttribute("usuarioNombre")));
        comentarioRepositorio.save(registro);
        atributos.addFlashAttribute("mensaje", "Comentario de calidad registrado."); return "redirect:/auditor";
    }

    @org.springframework.web.bind.annotation.PostMapping("/auditor/comentarios/{id}/estado")
    public String cerrarComentario(@org.springframework.web.bind.annotation.PathVariable String id,
            @org.springframework.web.bind.annotation.RequestParam String estado, HttpSession sesion, RedirectAttributes atributos) {
        if (!tieneRol(sesion, "AUDITOR")) { atributos.addFlashAttribute("error", "Acceso no autorizado."); return "redirect:/login"; }
        ComentarioCalidad c = comentarioRepositorio.findById(id).orElse(null);
        if (c == null || !("PENDIENTE".equals(estado) || "ATENDIDO".equals(estado))) {
            atributos.addFlashAttribute("error", "No se pudo actualizar el seguimiento."); return "redirect:/auditor";
        }
        c.setEstado(estado); comentarioRepositorio.save(c); atributos.addFlashAttribute("mensaje", "Seguimiento actualizado."); return "redirect:/auditor";
    }

    @org.springframework.web.bind.annotation.PostMapping("/auditor/comentarios/{id}/publicacion")
    public String publicarComentario(@org.springframework.web.bind.annotation.PathVariable String id,
            @org.springframework.web.bind.annotation.RequestParam boolean publicado, HttpSession sesion,
            RedirectAttributes atributos) {
        if (!tieneRol(sesion, "AUDITOR")) {
            atributos.addFlashAttribute("error", "Acceso no autorizado.");
            return "redirect:/login";
        }
        ComentarioCalidad c = comentarioRepositorio.findById(id).orElse(null);
        if (c == null) {
            atributos.addFlashAttribute("error", "No se encontró el comentario.");
            return "redirect:/auditor";
        }
        c.setPublicado(publicado);
        if (publicado) {
            c.setEstado("ATENDIDO");
        }
        comentarioRepositorio.save(c);
        atributos.addFlashAttribute("mensaje", publicado ? "Comentario aprobado y visible para los turistas." : "Comentario ocultado al público.");
        return "redirect:/auditor";
    }


    @GetMapping("/admin/observaciones")
    public String observacionesAdmin(HttpSession sesion, Model modelo, RedirectAttributes atributos) {
        if (!Boolean.TRUE.equals(sesion.getAttribute("admin"))) {
            atributos.addFlashAttribute("error", "Solo el administrador puede consultar las observaciones.");
            return "redirect:/login";
        }
        modelo.addAttribute("comentarios", comentarioRepositorio.findAllByOrderByFechaDesc());
        return "Admin/observaciones";
    }

    private boolean esRutaGeneral(String guia) {
        if (guia == null) return false;
        String valor = normalizar(guia);
        return valor.contains("guia") && (valor.contains("local") || valor.contains("naturaleza") || valor.contains("cultural"));
    }

    private boolean coincideResponsable(String responsable, String nombre, String correo) {
        if (responsable == null || responsable.isBlank()) return false;
        String valor = normalizar(responsable);
        return valor.equals(normalizar(nombre)) || valor.equals(normalizar(correo));
    }

    private String normalizar(String valor) {
        if (valor == null) return "";
        return Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT);
    }

    private boolean tieneRol(HttpSession sesion, String rolEsperado) {
        return sesion != null && rolEsperado.equals(sesion.getAttribute("usuarioRol"));
    }
}
