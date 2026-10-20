package com.crud.rutasvivas.repositorios;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.crud.rutasvivas.entidades.Usuario;

public interface UsuarioRepositorio extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);
}