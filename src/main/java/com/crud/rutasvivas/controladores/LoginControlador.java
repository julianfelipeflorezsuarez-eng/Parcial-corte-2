
package com.crud.rutasvivas.controladores;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crud.rutasvivas.entidades.Usuario;
import com.crud.rutasvivas.repositorios.UsuarioRepositorio;

@Controller
public class LoginControlador {

    private final UsuarioRepositorio usuarioRepositorio;

    @Value("${app.admin.usuario:admin}")
    private String usuarioAdmin;

    @Value("${app.admin.clave:admin123}")
    private String claveAdmin;

    public LoginControlador(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @GetMapping("/login")
    public String login(Model modelo) {
        if (!modelo.containsAttribute("usuario")) {
            modelo.addAttribute("usuario", new Usuario());
        }
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(
            String correo,
            String clave,
            HttpSession sesion,
            HttpServletRequest request,
            RedirectAttributes atributos) {

        if (correo == null || clave == null
                || correo.isBlank() || clave.isBlank()) {
            atributos.addFlashAttribute("error", "Escribe el usuario y la contraseña.");
            return "redirect:/login";
        }

        if (correo.equalsIgnoreCase(usuarioAdmin)
                && clave.equals(claveAdmin)) {
            request.changeSessionId();
            sesion.setAttribute("admin", Boolean.TRUE);
            sesion.setAttribute("usuarioRol", "ADMIN");
            sesion.setAttribute("usuarioNombre", "Administrador");
            return "redirect:/panel";
        }

        Usuario usuario = usuarioRepositorio
                .findByCorreoIgnoreCase(correo.trim())
                .orElse(null);

        if (usuario == null || !usuario.isActivo()
                || !coincideHash(clave, usuario.getClave())) {
            atributos.addFlashAttribute(
                    "error", "Credenciales incorrectas o cuenta inactiva.");
            return "redirect:/login";
        }

        request.changeSessionId();
        sesion.setAttribute("usuarioId", usuario.getId());
        sesion.setAttribute("usuarioNombre", usuario.getNombre());
        sesion.setAttribute("usuarioCorreo", usuario.getCorreo());
        sesion.setAttribute("usuarioRol", usuario.getRol());
        sesion.setAttribute("admin", Boolean.FALSE);

        return "redirect:/panel";
    }

    @GetMapping("/registro")
    public String registro(Model modelo) {
        if (!modelo.containsAttribute("usuario")) {
            modelo.addAttribute("usuario", new Usuario());
        }
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @Valid Usuario usuario,
            BindingResult resultado,
            Model modelo,
            RedirectAttributes atributos) {

        if (resultado.hasErrors()) {
            modelo.addAttribute("usuario", usuario);
            return "registro";
        }

        if (usuarioRepositorio.existsByCorreoIgnoreCase(usuario.getCorreo().trim())) {
            modelo.addAttribute("error", "Ya existe una cuenta con ese correo.");
            modelo.addAttribute("usuario", usuario);
            return "registro";
        }

        usuario.setId(null);
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setCorreo(usuario.getCorreo().trim().toLowerCase());
        usuario.setClave(hash(usuario.getClave()));
        usuario.setRol("TURISTA");
        usuario.setActivo(true);

        usuarioRepositorio.save(usuario);

        atributos.addFlashAttribute(
                "mensaje", "Tu cuenta fue creada. Ya puedes iniciar sesión.");

        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/";
    }

    @GetMapping("/panel")
    public String panel(HttpSession sesion, RedirectAttributes atributos) {
        String rol = (String) sesion.getAttribute("usuarioRol");

        if (rol == null) {
            atributos.addFlashAttribute("error", "Inicia sesión para acceder al panel.");
            return "redirect:/login";
        }

        return switch (rol) {
            case "ADMIN" -> "redirect:/atractivos";
            case "TURISTA" -> "redirect:/";
            case "GUIA" -> "redirect:/guia";
            case "GESTOR_CULTURAL" -> "redirect:/gestor-cultural";
            case "AUDITOR" -> "redirect:/auditor";
            default -> {
                sesion.invalidate();
                yield "redirect:/login";
            }
        };
    }

    private String hash(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo procesar la contraseña.", e);
        }
    }

    private boolean coincideHash(String texto, String hashGuardado) {
        if (hashGuardado == null) {
            return false;
        }
        return MessageDigest.isEqual(
                hash(texto).getBytes(StandardCharsets.UTF_8),
                hashGuardado.getBytes(StandardCharsets.UTF_8));
    }
}
