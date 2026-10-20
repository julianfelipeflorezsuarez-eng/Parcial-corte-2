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

import com.crud.rutasvivas.entidades.Atractivo;
import com.crud.rutasvivas.repositorios.AtractivoRepositorio;
import com.crud.rutasvivas.repositorios.RutaRepositorio;

@RestController
@RequestMapping("/api/atractivos")
public class AtractivoRestControlador {

    private final AtractivoRepositorio atractivoRepositorio;
    private final RutaRepositorio rutaRepositorio;

    public AtractivoRestControlador(
            AtractivoRepositorio atractivoRepositorio,
            RutaRepositorio rutaRepositorio) {
        this.atractivoRepositorio = atractivoRepositorio;
        this.rutaRepositorio = rutaRepositorio;
    }

    @GetMapping
    public List<Atractivo> listar() {
        return atractivoRepositorio.findByPublicadoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable String id) {
        return atractivoRepositorio.findById(id)
                .filter(Atractivo::isPublicado)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody Atractivo atractivo,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Los datos del atractivo no son válidos."));
        }

        atractivo.setId(null);
        Atractivo guardado = atractivoRepositorio.save(atractivo);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable String id,
            @Valid @RequestBody Atractivo atractivo,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Los datos del atractivo no son válidos."));
        }

        if (!atractivoRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        atractivo.setId(id);
        return ResponseEntity.ok(atractivoRepositorio.save(atractivo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {

        if (!atractivoRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (rutaRepositorio.existsByAtractivosIdsContaining(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("error",
                            "El atractivo pertenece a una ruta y no puede eliminarse."));
        }

        atractivoRepositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}