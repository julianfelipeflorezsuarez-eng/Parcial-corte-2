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

import com.crud.rutasvivas.entidades.Experiencia;
import com.crud.rutasvivas.repositorios.ExperienciaRepositorio;

@RestController
@RequestMapping("/api/experiencias")
public class ExperienciaRestControlador {

    private final ExperienciaRepositorio experienciaRepositorio;

    public ExperienciaRestControlador(
            ExperienciaRepositorio experienciaRepositorio) {
        this.experienciaRepositorio = experienciaRepositorio;
    }

    @GetMapping
    public List<Experiencia> listar() {
        return experienciaRepositorio.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Experiencia> buscar(@PathVariable String id) {
        return experienciaRepositorio.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody Experiencia experiencia,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error",
                            "Los datos de la experiencia no son válidos."));
        }

        experiencia.setId(null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(experienciaRepositorio.save(experiencia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable String id,
            @Valid @RequestBody Experiencia experiencia,
            BindingResult resultado) {

        if (!experienciaRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (resultado.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error",
                            "Los datos de la experiencia no son válidos."));
        }

        experiencia.setId(id);
        return ResponseEntity.ok(experienciaRepositorio.save(experiencia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        if (!experienciaRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        experienciaRepositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}