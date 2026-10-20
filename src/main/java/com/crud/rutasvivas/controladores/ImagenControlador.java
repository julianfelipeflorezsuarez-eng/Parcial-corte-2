package com.crud.rutasvivas.controladores;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ImagenControlador {

    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of(".jpg", ".jpeg", ".png", ".webp");

    private final Path carpetaBase = Paths.get(
            System.getenv().getOrDefault("UPLOADS_DIR", "uploads"))
            .toAbsolutePath().normalize();

    public String guardarImagen(MultipartFile archivo, String tipo)
            throws IOException {

        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        if (!"atractivos".equals(tipo) && !"experiencias".equals(tipo)) {
            throw new IllegalArgumentException("Tipo de imagen no válido.");
        }

        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null) {
            throw new IllegalArgumentException("El archivo no tiene nombre.");
        }

        String nombre = Paths.get(nombreOriginal).getFileName().toString();
        int punto = nombre.lastIndexOf('.');
        if (punto < 0) {
            throw new IllegalArgumentException("Selecciona una imagen JPG, JPEG, PNG o WEBP.");
        }

        String extension = nombre.substring(punto).toLowerCase();
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new IllegalArgumentException("Selecciona una imagen JPG, JPEG, PNG o WEBP.");
        }
        if (archivo.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("La imagen no puede superar los 5 MB.");
        }

        Path carpeta = carpetaBase.resolve(tipo).normalize();
        Files.createDirectories(carpeta);
        String nombreFinal = UUID.randomUUID() + extension;
        Path destino = carpeta.resolve(nombreFinal).normalize();
        if (!destino.startsWith(carpeta)) {
            throw new IllegalArgumentException("Nombre de archivo no válido.");
        }

        archivo.transferTo(destino);
        return "/imagenes/" + tipo + "/" + nombreFinal;
    }

    @GetMapping("/imagenes/{tipo}/{nombre}")
    @ResponseBody
    public Resource verImagen(@PathVariable String tipo,
                              @PathVariable String nombre) throws IOException {
        if ((!"atractivos".equals(tipo) && !"experiencias".equals(tipo))
                || nombre.contains("/") || nombre.contains("\\") || nombre.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Path carpeta = carpetaBase.resolve(tipo).normalize();
        Path archivo = carpeta.resolve(nombre).normalize();
        if (!archivo.startsWith(carpeta) || !Files.isRegularFile(archivo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Resource recurso = new UrlResource(archivo.toUri());
        if (!recurso.exists() || !recurso.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return recurso;
    }
}
