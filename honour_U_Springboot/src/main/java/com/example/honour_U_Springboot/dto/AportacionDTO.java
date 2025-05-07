package com.example.honour_U_Springboot.dto;

import com.example.honour_U_Springboot.model.Aportacion;

public class AportacionDTO {
    private Long aportacionId;
    private String mensaje;
    private String remitente;
    private String url;
    public enum MediaType { VIDEO, AUDIO, FOTO, TEXTO }
    private Aportacion.MediaType mediaType;
    private boolean esVisible;

    //Constructor


    public AportacionDTO(Aportacion aportacion) {
        this.aportacionId = aportacion.getAportacionId();
        this.mensaje = aportacion.getMensaje();
        this.remitente = aportacion.getRemitente();
        this.url = aportacion.getUrl();
        this.mediaType = aportacion.getMediaType();
        this.esVisible = aportacion.isEsVisible();
    }
    // Método para convertir el DTO a la entidad Aportacion
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
}
