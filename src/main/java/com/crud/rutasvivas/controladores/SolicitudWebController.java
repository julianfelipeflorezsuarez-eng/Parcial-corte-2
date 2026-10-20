
package com.crud.rutasvivas.controladores;

import java.time.LocalDateTime;
import java.text.Normalizer;
import java.util.Locale;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crud.rutasvivas.entidades.Ruta;
import com.crud.rutasvivas.entidades.Experiencia;
import com.crud.rutasvivas.repositorios.ExperienciaRepositorio;
import com.crud.rutasvivas.entidades.Solicitud;
import com.crud.rutasvivas.repositorios.RutaRepositorio;
import com.crud.rutasvivas.repositorios.SolicitudRepositorio;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudWebController {

    private final SolicitudRepositorio solicitudRepositorio;
    private final RutaRepositorio rutaRepositorio;
    private final ExperienciaRepositorio experienciaRepositorio;

    public SolicitudWebController(
            SolicitudRepositorio solicitudRepositorio,
            RutaRepositorio rutaRepositorio,
            ExperienciaRepositorio experienciaRepositorio) {
        this.solicitudRepositorio = solicitudRepositorio;
        this.rutaRepositorio = rutaRepositorio;
        this.experienciaRepositorio = experienciaRepositorio;
    }

    @GetMapping("/nueva")
    public String nueva(
            @RequestParam(required = false) String rutaId,
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!esTurista(sesion)) {
            atributos.addFlashAttribute("error", "Inicia sesión como turista para solicitar información.");
            return "redirect:/login";
        }

        if (rutaId == null || rutaId.isBlank()
                || !rutaRepositorio.existsById(rutaId)) {
            atributos.addFlashAttribute("error", "Selecciona una ruta válida.");
            return "redirect:/rutas";
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setRutaId(rutaId);

        modelo.addAttribute("solicitud", solicitud);
        modelo.addAttribute("ruta", rutaRepositorio.findById(rutaId).orElseThrow());
        modelo.addAttribute("esExperiencia", false);

        return "Solicitud/formulario";
    }

    @GetMapping("/nueva-experiencia")
    public String nuevaExperiencia(
            @RequestParam String experienciaId, HttpSession sesion,
            Model modelo, RedirectAttributes atributos) {
        if (!esTurista(sesion)) {
            atributos.addFlashAttribute("error", "Inicia sesión como turista para solicitar una experiencia.");
            return "redirect:/login";
        }
        Experiencia experiencia = experienciaRepositorio.findById(experienciaId).orElse(null);
        if (experiencia == null) {
            atributos.addFlashAttribute("error", "Selecciona una experiencia válida.");
            return "redirect:/experiencias";
        }
        Solicitud solicitud = new Solicitud();
        solicitud.setTipoSolicitud("EXPERIENCIA");
        solicitud.setExperienciaId(experienciaId);
        if (experiencia.getRutaId() != null && !experiencia.getRutaId().isBlank()) {
            solicitud.setRutaId(experiencia.getRutaId());
        }
        modelo.addAttribute("solicitud", solicitud);
        modelo.addAttribute("experiencia", experiencia);
        modelo.addAttribute("esExperiencia", true);
        return "Solicitud/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Solicitud solicitud,
            BindingResult resultado,
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!esTurista(sesion)) {
            atributos.addFlashAttribute("error", "Inicia sesión como turista antes de enviar una solicitud.");
            return "redirect:/login";
        }

        boolean esExperiencia = "EXPERIENCIA".equals(solicitud.getTipoSolicitud());
        Ruta ruta = solicitud.getRutaId() == null || solicitud.getRutaId().isBlank()
                ? null : rutaRepositorio.findById(solicitud.getRutaId()).orElse(null);
        Experiencia experiencia = esExperiencia && solicitud.getExperienciaId() != null
                ? experienciaRepositorio.findById(solicitud.getExperienciaId()).orElse(null) : null;

        if (esExperiencia && experiencia == null) {
            resultado.reject("experiencia.noexiste", "La experiencia seleccionada ya no existe.");
        } else if (!esExperiencia && ruta == null) {
            resultado.rejectValue("rutaId", "ruta.noexiste", "La ruta seleccionada ya no existe.");
        }

        if (resultado.hasErrors()) {
            modelo.addAttribute("solicitud", solicitud);
            modelo.addAttribute("ruta", ruta);
            modelo.addAttribute("experiencia", experiencia);
            modelo.addAttribute("esExperiencia", esExperiencia);
            return "Solicitud/formulario";
        }

        solicitud.setId(null);
        solicitud.setTipoSolicitud(esExperiencia ? "EXPERIENCIA" : "RUTA");
        solicitud.setEstado("PENDIENTE");
        solicitud.setFechaCreacion(LocalDateTime.now());

        solicitudRepositorio.save(solicitud);

        atributos.addFlashAttribute(
                "mensaje",
                "Tu solicitud de información fue enviada correctamente. Usa el mismo nombre y correo para consultar la respuesta.");
        atributos.addAttribute("nombre", solicitud.getNombre());
        atributos.addAttribute("contacto", solicitud.getContacto());
        return "redirect:/solicitudes/mis-solicitudes";
    }

    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(@RequestParam(required = false) String nombre,
            @RequestParam(required = false) String contacto, HttpSession sesion, Model modelo) {
        if (!esTurista(sesion)) {
            return "redirect:/login";
        }
        modelo.addAttribute("nombre", nombre);
        modelo.addAttribute("contacto", contacto);
        if (nombre != null && !nombre.isBlank() && contacto != null && !contacto.isBlank()) {
            modelo.addAttribute("solicitudes", solicitudRepositorio
                    .findByNombreIgnoreCaseAndContactoIgnoreCaseOrderByFechaCreacionDesc(
                            nombre.trim(), contacto.trim()));
            modelo.addAttribute("consultado", true);
        }
        modelo.addAttribute("rutas", rutaRepositorio.findAll());
        modelo.addAttribute("experiencias", experienciaRepositorio.findAll());
        return "Solicitud/mis-solicitudes";
    }

    @GetMapping
    public String listar(
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute(
                    "error", "Solo el administrador puede consultar las solicitudes.");
            return "redirect:/login";
        }

        modelo.addAttribute(
                "solicitudes",
                solicitudRepositorio.findAllByOrderByFechaCreacionDesc());

        modelo.addAttribute("rutas", rutaRepositorio.findAll());
        modelo.addAttribute("experiencias", experienciaRepositorio.findAll());

        return "Solicitud/listar";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable String id,
            @RequestParam String estado,
            HttpSession sesion,
            RedirectAttributes atributos) {

        boolean admin = esAdmin(sesion);
        boolean guia = tieneRol(sesion, "GUIA");
        boolean gestor = tieneRol(sesion, "GESTOR_CULTURAL");
        if (!admin && !guia && !gestor) {
            atributos.addFlashAttribute("error", "No tienes permiso para esta acción.");
            return "redirect:/login";
        }

        if (!estado.equals("PENDIENTE") && !estado.equals("ATENDIDA") && !estado.equals("DESCARTADA")) {
            atributos.addFlashAttribute("error", "El estado seleccionado no es válido.");
            return redireccionPanel(admin, guia, gestor);
        }

        Solicitud solicitud = solicitudRepositorio.findById(id).orElse(null);
        if (solicitud == null) {
            atributos.addFlashAttribute("error", "La solicitud no existe.");
            return redireccionPanel(admin, guia, gestor);
        }

        if (!admin && guia && !esSolicitudDeGuia(solicitud, sesion)) {
            atributos.addFlashAttribute("error", "Solo puedes gestionar solicitudes de tus rutas.");
            return "redirect:/guia";
        }
        if (!admin && gestor && !esSolicitudDeGestor(solicitud, sesion)) {
            atributos.addFlashAttribute("error", "Solo puedes gestionar solicitudes de tus experiencias.");
            return "redirect:/gestor-cultural";
        }

        solicitud.setEstado(estado);
        solicitudRepositorio.save(solicitud);
        atributos.addFlashAttribute("mensaje", "El estado de la solicitud fue actualizado.");
        return redireccionPanel(admin, guia, gestor);
    }

    @PostMapping("/{id}/responder")
    public String responder(@PathVariable String id, @RequestParam String respuesta,
            HttpSession sesion, RedirectAttributes atributos) {
        boolean admin = esAdmin(sesion);
        boolean guia = tieneRol(sesion, "GUIA");
        boolean gestor = tieneRol(sesion, "GESTOR_CULTURAL");
        if (!admin && !guia && !gestor) {
            atributos.addFlashAttribute("error", "No tienes permiso para responder solicitudes.");
            return "redirect:/login";
        }
        if (respuesta == null || respuesta.isBlank() || respuesta.trim().length() > 1500) {
            atributos.addFlashAttribute("error", "Escribe una respuesta de hasta 1500 caracteres.");
            return redireccionPanel(admin, guia, gestor);
        }
        Solicitud solicitud = solicitudRepositorio.findById(id).orElse(null);
        if (solicitud == null) {
            atributos.addFlashAttribute("error", "La solicitud no existe.");
            return redireccionPanel(admin, guia, gestor);
        }
        if (!admin && guia && !esSolicitudDeGuia(solicitud, sesion)) {
            atributos.addFlashAttribute("error", "Solo puedes responder solicitudes de tus rutas.");
            return "redirect:/guia";
        }
        if (!admin && gestor && !esSolicitudDeGestor(solicitud, sesion)) {
            atributos.addFlashAttribute("error", "Solo puedes responder solicitudes de tus experiencias.");
            return "redirect:/gestor-cultural";
        }
        solicitud.setRespuestaGuia(respuesta.trim());
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitud.setEstado("ATENDIDA");
        solicitudRepositorio.save(solicitud);
        atributos.addFlashAttribute("mensaje", "Respuesta guardada correctamente.");
        return redireccionPanel(admin, guia, gestor);
    }

    private boolean esSolicitudDeGuia(Solicitud solicitud, HttpSession sesion) {
        Ruta ruta = null;
        if (solicitud.getRutaId() != null && !solicitud.getRutaId().isBlank()) {
            ruta = rutaRepositorio.findById(solicitud.getRutaId()).orElse(null);
        }
        if (ruta == null && solicitud.getExperienciaId() != null) {
            Experiencia experiencia = experienciaRepositorio.findById(solicitud.getExperienciaId()).orElse(null);
            if (experiencia != null && experiencia.getRutaId() != null) {
                ruta = rutaRepositorio.findById(experiencia.getRutaId()).orElse(null);
            }
        }
        return ruta != null && esRutaDelGuia(ruta, sesion);
    }

    private boolean esSolicitudDeGestor(Solicitud solicitud, HttpSession sesion) {
        if ((!"EXPERIENCIA".equalsIgnoreCase(solicitud.getTipoSolicitud())
                && (solicitud.getExperienciaId() == null || solicitud.getExperienciaId().isBlank()))
                || solicitud.getExperienciaId() == null) return false;
        Experiencia experiencia = experienciaRepositorio.findById(solicitud.getExperienciaId()).orElse(null);
        if (experiencia == null) return false;
        // Las solicitudes de experiencias se atienden desde el panel del gestor cultural.
        // El catálogo incluye responsables locales que no necesariamente tienen una cuenta.
        return true;
    }

    private String redireccionPanel(boolean admin, boolean guia, boolean gestor) {
        if (admin) return "redirect:/solicitudes";
        if (guia) return "redirect:/guia";
        if (gestor) return "redirect:/gestor-cultural";
        return "redirect:/login";
    }

    private boolean esRutaDelGuia(Ruta ruta, HttpSession sesion) {
        if (ruta == null) return false;
        String nombre = String.valueOf(sesion.getAttribute("usuarioNombre"));
        String correo = String.valueOf(sesion.getAttribute("usuarioCorreo"));
        return coincideResponsable(ruta.getGuia(), nombre, correo) || esRutaGeneral(ruta.getGuia());
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
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    private boolean tieneRol(HttpSession sesion, String rol) {
        return sesion != null && rol.equals(sesion.getAttribute("usuarioRol"));
    }

    private boolean esAdmin(HttpSession sesion) {
        return sesion != null && Boolean.TRUE.equals(sesion.getAttribute("admin"));
    }

    private boolean esTurista(HttpSession sesion) {
        return tieneRol(sesion, "TURISTA");
    }
}
