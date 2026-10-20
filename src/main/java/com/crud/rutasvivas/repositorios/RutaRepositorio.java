package com.crud.rutasvivas.repositorios;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.crud.rutasvivas.entidades.Ruta;

public interface RutaRepositorio extends MongoRepository<Ruta, String> {

    List<Ruta> findAllBy(Pageable pageable);

    boolean existsByAtractivosIdsContaining(String atractivoId);
    boolean existsByNombreIgnoreCase(String nombre);
}