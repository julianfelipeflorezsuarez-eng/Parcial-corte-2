package com.crud.rutasvivas.repositorios;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.crud.rutasvivas.entidades.Atractivo;

public interface AtractivoRepositorio
        extends MongoRepository<Atractivo, String> {

    List<Atractivo> findByPublicadoTrue();

    List<Atractivo> findByPublicadoTrue(Pageable pageable);

    List<Atractivo> findByDepartamentoIgnoreCaseAndPublicadoTrue(
            String departamento);

    List<Atractivo> findByDepartamentoIgnoreCaseAndMunicipioIgnoreCaseAndPublicadoTrue(
            String departamento, String municipio);

    List<Atractivo> findByDepartamentoIgnoreCase(
            String departamento);
    boolean existsByNombreIgnoreCase(String nombre);
}