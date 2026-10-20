package com.crud.rutasvivas.entidades;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rutas")
public class Ruta {

    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
    private String descripcion;

    @NotBlank(message = "El destino es obligatorio")
    private String destino;

    @NotBlank(message = "La duración es obligatoria")
    private String duracion;

    @NotBlank(message = "El guía es obligatorio")
    private String guia;

    private List<String> atractivosIds = new ArrayList<>();

    public Ruta() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getGuia() {
        return guia;
    }

    public void setGuia(String guia) {
        this.guia = guia;
    }

    public List<String> getAtractivosIds() {
        return atractivosIds;
    }

    public void setAtractivosIds(List<String> atractivosIds) {
        this.atractivosIds = atractivosIds == null
                ? new ArrayList<>()
                : new ArrayList<>(atractivosIds);
    }
}