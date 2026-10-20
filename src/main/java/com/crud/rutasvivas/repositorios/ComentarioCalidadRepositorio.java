package com.crud.rutasvivas.repositorios;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.crud.rutasvivas.entidades.ComentarioCalidad;
public interface ComentarioCalidadRepositorio extends MongoRepository<ComentarioCalidad, String> {
    List<ComentarioCalidad> findAllByOrderByFechaDesc();
    List<ComentarioCalidad> findByTipoContenidoAndContenidoIdAndPublicadoTrueOrderByFechaDesc(String tipoContenido, String contenidoId);
}
