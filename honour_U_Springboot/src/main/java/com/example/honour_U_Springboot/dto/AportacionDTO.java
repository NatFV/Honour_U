package com.example.honour_U_Springboot.dto;

public class AportacionDTO {
    private Long aportacionId;
    private String mensaje;
    private String remitente;
    private String url;
    public enum MediaType { VIDEO, AUDIO, FOTO, TEXTO }
    private MediaType mediaType;
    private boolean esVisible;

    //Constructor


    public AportacionDTO(Long aportacionId, String mensaje, String remitente, String url, MediaType mediaType, boolean esVisible) {
        this.aportacionId = aportacionId;
        this.mensaje = mensaje;
        this.remitente = remitente;
        this.url = url;
        this.mediaType = mediaType;
        this.esVisible = esVisible;
    }

    //Getters y Setters

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

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isEsVisible() {
        return esVisible;
    }

    public void setEsVisible(boolean esVisible) {
        this.esVisible = esVisible;
    }
}
