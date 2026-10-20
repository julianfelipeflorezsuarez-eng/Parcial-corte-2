package com.crud.rutasvivas.controladores;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crud.rutasvivas.entidades.Ruta;
import com.crud.rutasvivas.repositorios.AtractivoRepositorio;
import com.crud.rutasvivas.repositorios.RutaRepositorio;

@RestController
@RequestMapping("/api/rutas")
public class RutaRestControlador {

    private final RutaRepositorio rutaRepositorio;
    private final AtractivoRepositorio atractivoRepositorio;

    public RutaRestControlador(
            RutaRepositorio rutaRepositorio,
            AtractivoRepositorio atractivoRepositorio) {
        this.rutaRepositorio = rutaRepositorio;
        this.atractivoRepositorio = atractivoRepositorio;
    }

    @GetMapping
    public List<Ruta> listar() {
        return rutaRepositorio.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ruta> buscar(@PathVariable String id) {
        return rutaRepositorio.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody Ruta ruta,
            BindingResult resultado) {

        ResponseEntity<?> error = validarRuta(ruta, resultado);

        if (error != null) {
            return error;
        }

        ruta.setId(null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rutaRepositorio.save(ruta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable String id,
            @Valid @RequestBody Ruta ruta,
            BindingResult resultado) {

        if (!rutaRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<?> error = validarRuta(ruta, resultado);

        if (error != null) {
            return error;
        }

        ruta.setId(id);
        return ResponseEntity.ok(rutaRepositorio.save(ruta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        if (!rutaRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        rutaRepositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> validarRuta(
            Ruta ruta,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Los datos de la ruta no son válidos."));
        }

        if (ruta.getAtractivosIds() == null
                || ruta.getAtractivosIds().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error",
                            "La ruta debe contener al menos un atractivo."));
        }

        for (String id : ruta.getAtractivosIds()) {
            if (!atractivoRepositorio.existsById(id)) {
                return ResponseEntity.badRequest().body(
                        Map.of("error",
                                "Uno de los atractivos seleccionados no existe."));
            }
        }

        return null;
    }
}