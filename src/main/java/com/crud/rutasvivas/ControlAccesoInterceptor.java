package com.crud.rutasvivas;

import java.io.IOException;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ControlAccesoInterceptor implements HandlerInterceptor {

    private static final Pattern GESTION_ADMIN = Pattern.compile(
            "^/(atractivos|rutas)(/nuevo|/[^/]+/editar|/guardar|/[^/]+/eliminar)$");
    private static final Pattern GESTION_EXPERIENCIAS = Pattern.compile(
            "^/experiencias(/nuevo|/[^/]+/editar|/guardar|/[^/]+/eliminar)$");

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws IOException {

        String uri = request.getRequestURI();
        String metodo = request.getMethod();

        // Consultar catálogos es público, pero solicitar recorridos o experiencias
        // y consultar las solicitudes propias requiere una sesión de turista.
        if (uri.equals("/solicitudes/nueva")
                || uri.equals("/solicitudes/nueva-experiencia")
                || uri.equals("/solicitudes/guardar")
                || uri.equals("/solicitudes/mis-solicitudes")) {
            HttpSession sesionSolicitud = request.getSession(false);
            String rolSolicitud = sesionSolicitud == null ? null
                    : (String) sesionSolicitud.getAttribute("usuarioRol");
            if (!"TURISTA".equals(rolSolicitud)) {
                if (sesionSolicitud == null || rolSolicitud == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                } else {
                    response.sendRedirect(request.getContextPath() + "/panel");
                }
                return false;
            }
            return true;
        }

        if (uri.equals("/login") || uri.equals("/registro") || uri.equals("/logout")) {
            return true;
        }

        if (uri.startsWith("/css/") || uri.startsWith("/js/")
                || uri.startsWith("/img/") || uri.startsWith("/imagenes/")
                || uri.startsWith("/webjars/") || uri.equals("/favicon.ico")) {
            return true;
        }

        HttpSession sesion = request.getSession(false);
        String rol = sesion == null ? null : (String) sesion.getAttribute("usuarioRol");
        boolean admin = sesion != null && Boolean.TRUE.equals(sesion.getAttribute("admin"));
        boolean gestor = "GESTOR_CULTURAL".equals(rol);
        boolean escritura = "POST".equals(metodo) || "PUT".equals(metodo)
                || "PATCH".equals(metodo) || "DELETE".equals(metodo);

        boolean gestionAdmin = GESTION_ADMIN.matcher(uri).matches();
        boolean gestionExperiencias = GESTION_EXPERIENCIAS.matcher(uri).matches();
        boolean escrituraApi = uri.startsWith("/api/") && escritura;

        if ((gestionAdmin && !admin) || (gestionExperiencias && !admin && !gestor)
                || (escrituraApi && !admin)) {
            if (uri.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"No tienes permisos para realizar esta operación\"}");
                return false;
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        return true;
    }
}
