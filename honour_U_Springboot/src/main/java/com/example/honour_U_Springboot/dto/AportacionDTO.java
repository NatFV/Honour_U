package com.example.honour_U_Springboot.dto;

import com.example.honour_U_Springboot.model.Aportacion;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.web.multipart.MultipartFile;

/**
 * Clase Aportación DTO utilizada para las consultas API
 * @author Natalia
 * @version 1
 */
public class AportacionDTO {
    private Long aportacionId;
    private String mensaje;
    private String remitente;
    private String url;
    public enum MediaType { VIDEO, AUDIO, FOTO, TEXTO }
    private Aportacion.MediaType mediaType;
    private boolean esVisible;
    private MultipartFile archivo; //Este es un nuevo campo para subir archivos

    //Constructor
    // Constructor vacío necesario para la deserialización JSON (Jackson)
    @JsonCreator
    public AportacionDTO() {
    }

    public AportacionDTO(Aportacion aportacion) {
        this.aportacionId = aportacion.getAportacionId();
        this.mensaje = aportacion.getMensaje();
        this.remitente = aportacion.getRemitente();
        this.url = aportacion.getUrl();
        this.mediaType = aportacion.getMediaType();
        this.esVisible = aportacion.isEsVisible();

    }

    //Getters and setters


    public Long getAportacionId() {
        return aportacionId;
    }

    public void setAportacionId(Long aportacionId) {
        this.aportacionId = aportacionId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Aportacion.MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(Aportacion.MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isEsVisible() {
        return esVisible;
    }

    public void setEsVisible(boolean esVisible) {
        this.esVisible = esVisible;
    }

    public MultipartFile getArchivo() {
        return archivo;
    }

    public void setArchivo(MultipartFile archivo) {
        this.archivo = archivo;
    }

    /**
     * Método toEntity
     * Copia los valores de los atributos del DTO al nuevo objeto Aportacion
     * @return el nuevo objeto Aportacion
     */
    public Aportacion toEntity() {
        Aportacion aportacion = new Aportacion();
        aportacion.setAportacionId(this.aportacionId);
        aportacion.setMensaje(this.mensaje);
        aportacion.setRemitente(this.remitente);
        aportacion.setUrl(this.url);
        aportacion.setMediaType(this.mediaType);
        aportacion.setEsVisible(this.esVisible);
        return aportacion;
    }
}
