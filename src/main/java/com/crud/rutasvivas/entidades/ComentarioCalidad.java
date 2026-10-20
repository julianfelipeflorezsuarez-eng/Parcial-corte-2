package com.crud.rutasvivas.entidades;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comentarios_calidad")
public class ComentarioCalidad {
    @Id private String id;
    private String tipoContenido;
    private String contenidoId;
    private String nombreContenido;
    private String comentario;
    private String autor;
    private String estado = "PENDIENTE";
    private boolean publicado = false;
    private LocalDateTime fecha = LocalDateTime.now();
    public ComentarioCalidad() {}
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getTipoContenido(){return tipoContenido;} public void setTipoContenido(String v){tipoContenido=v;}
    public String getContenidoId(){return contenidoId;} public void setContenidoId(String v){contenidoId=v;}
    public String getNombreContenido(){return nombreContenido;} public void setNombreContenido(String v){nombreContenido=v;}
    public String getComentario(){return comentario;} public void setComentario(String v){comentario=v;}
    public String getAutor(){return autor;} public void setAutor(String v){autor=v;}
    public String getEstado(){return estado;} public void setEstado(String v){estado=v;}
    public boolean isPublicado(){return publicado;} public void setPublicado(boolean v){publicado=v;}
    public LocalDateTime getFecha(){return fecha;} public void setFecha(LocalDateTime v){fecha=v;}
}
