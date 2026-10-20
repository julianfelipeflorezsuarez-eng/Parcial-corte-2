
package com.crud.rutasvivas.repositorios;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.crud.rutasvivas.entidades.Solicitud;

public interface SolicitudRepositorio extends MongoRepository<Solicitud, String> {

    List<Solicitud> findByEstadoOrderByFechaCreacionDesc(String estado);

    List<Solicitud> findAllByOrderByFechaCreacionDesc();

    List<Solicitud> findByRutaId(String rutaId);

    List<Solicitud> findByNombreIgnoreCaseAndContactoIgnoreCaseOrderByFechaCreacionDesc(
            String nombre, String contacto);
}
