
package com.crud.rutasvivas.entidades;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Document(collection = "solicitudes")
public class Solicitud {

    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El contacto es obligatorio")
    @Email(message = "Ingresa un correo válido")
    private String contacto;

    private String rutaId;
    private String experienciaId;
    private String tipoSolicitud = "RUTA";

    @NotNull(message = "Selecciona una fecha")
    private LocalDate fechaDeseada;

    @Min(value = 1, message = "Debe haber al menos un participante")
    private int participantes = 1;

    private String mensaje;
    private String respuestaGuia;
    private LocalDateTime fechaRespuesta;
    private String estado = "PENDIENTE";
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public Solicitud() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getRutaId() { return rutaId; }
    public void setRutaId(String rutaId) { this.rutaId = rutaId; }

    public String getExperienciaId() { return experienciaId; }
    public void setExperienciaId(String experienciaId) { this.experienciaId = experienciaId; }

    public String getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(String tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }

    public LocalDate getFechaDeseada() { return fechaDeseada; }
    public void setFechaDeseada(LocalDate fechaDeseada) {
        this.fechaDeseada = fechaDeseada;
    }

    public int getParticipantes() { return participantes; }
    public void setParticipantes(int participantes) {
        this.participantes = participantes;
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getRespuestaGuia() { return respuestaGuia; }
    public void setRespuestaGuia(String respuestaGuia) { this.respuestaGuia = respuestaGuia; }
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
