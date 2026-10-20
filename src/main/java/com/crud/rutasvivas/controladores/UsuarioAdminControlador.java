
package com.crud.rutasvivas.controladores;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crud.rutasvivas.entidades.Usuario;
import com.crud.rutasvivas.repositorios.UsuarioRepositorio;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminControlador {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioAdminControlador(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @GetMapping
    public String listar(
            HttpSession sesion,
            Model modelo,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute("error", "Solo el administrador puede gestionar perfiles.");
            return "redirect:/login";
        }

        modelo.addAttribute("usuarios", usuarioRepositorio.findAll());
        return "Admin/usuarios";
    }

    @PostMapping("/rol")
    public String cambiarRol(
            @RequestParam String id,
            @RequestParam String rol,
            HttpSession sesion,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute("error", "No tienes permiso para cambiar perfiles.");
            return "redirect:/login";
        }

        if (!rol.equals("TURISTA")
                && !rol.equals("GUIA")
                && !rol.equals("GESTOR_CULTURAL")
                && !rol.equals("AUDITOR")) {
            atributos.addFlashAttribute("error", "El perfil seleccionado no es válido.");
            return "redirect:/admin/usuarios";
        }

        Usuario usuario = usuarioRepositorio.findById(id).orElse(null);

        if (usuario == null) {
            atributos.addFlashAttribute("error", "No se encontró la cuenta.");
            return "redirect:/admin/usuarios";
        }

        usuario.setRol(rol);
        usuarioRepositorio.save(usuario);

        atributos.addFlashAttribute("mensaje", "El perfil del usuario fue actualizado.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/estado")
    public String cambiarEstado(
            @RequestParam String id,
            @RequestParam boolean activo,
            HttpSession sesion,
            RedirectAttributes atributos) {

        if (!esAdmin(sesion)) {
            atributos.addFlashAttribute("error", "No tienes permiso para cambiar cuentas.");
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepositorio.findById(id).orElse(null);

        if (usuario == null) {
            atributos.addFlashAttribute("error", "No se encontró la cuenta.");
            return "redirect:/admin/usuarios";
        }

        usuario.setActivo(activo);
        usuarioRepositorio.save(usuario);

        atributos.addFlashAttribute("mensaje", "El estado de la cuenta fue actualizado.");
        return "redirect:/admin/usuarios";
    }

    private boolean esAdmin(HttpSession sesion) {
        return Boolean.TRUE.equals(sesion.getAttribute("admin"));
    }
}
