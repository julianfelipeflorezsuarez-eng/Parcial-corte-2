package com.crud.rutasvivas.repositorios;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.crud.rutasvivas.entidades.Experiencia;

public interface ExperienciaRepositorio
        extends MongoRepository<Experiencia, String> {

    List<Experiencia> findAllBy(Pageable pageable);
    boolean existsByNombreIgnoreCase(String nombre);
}