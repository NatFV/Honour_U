package com.example.honour_U_Springboot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Aportación
 * Representa las aportaciones de los usuarios
 */
@Entity //la clase es una entidad y será mapeada en tabla
//Cuando utilicé @Data me generó ecursión infinita entre los métodos hashCode() de las clases Proyecto y Aportacion
//Usando las notaciones de getter y setter para evitarlo
@Table(name = "aportacion")

@NoArgsConstructor //Genera constructor sin parámetros para Hibernate
public class Aportacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "aportacion_id", nullable = false)
    private Long aportacionId;
    @Column(name= "mensaje", nullable = false)
    private String mensaje;
    @Column(name= "remitente", nullable = false)
    private String remitente;
    @Column(name= "url", nullable = true)
    private String url;
    public enum MediaType { VIDEO, AUDIO, FOTO, TEXTO }
    @Enumerated(EnumType.STRING)
    @Column (name = "media_type")
    private MediaType mediaType;
    @Column (name = "es_visible")
    private boolean esVisible;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "aportacion_media", joinColumns = @JoinColumn(name = "aportacion_id"))
    @Column(name = "media_url", length = 512)
    @OrderColumn(name = "orden")
    private List<String> mediaUrls = new ArrayList<>();

    //Añadimos un ownerkey para que cada persona que hace una aportación la pueda manejar
    @Column(name = "owner_key", length = 64)
    private String ownerKey;

    //Añadimos un orden para que el administración pueda manejar las aportaciones
    @Column(name = "orden")
    private Integer orden;

    //Añadimos un enum para poder distinguir páginas especiales (normal, portada,índice) de páginas normales
    public enum PageType { NORMAL, PORTADA, INDICE, BLANCA }

    @Enumerated(EnumType.STRING)
    @Column(name = "page_type", nullable = false)
    private PageType pageType = PageType.NORMAL;

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public List<String> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }


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

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public String getOwnerKey() { return ownerKey; }
    public void setOwnerKey(String ownerKey) { this.ownerKey = ownerKey; }

    public PageType getPageType() { return pageType; }
    public void setPageType(PageType pageType) { this.pageType = pageType; }


    @Override
    public String toString() {
        return "Aportacion{" +
                "aportacionId=" + aportacionId +
                ", mensaje='" + mensaje + '\'' +
                ", remitente='" + remitente + '\'' +
                ", url='" + url + '\'' +
                ", mediaType=" + mediaType +
                ", esVisible=" + esVisible +
                '}';
    }

    //Relación Aportación-Proyecto: uno a muchos bidireccional
    //Aportación es el lado de muchos
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "proyecto_id")//nombre de la columna
    @JsonBackReference
    private Proyecto proyecto;

   // Relación Aportación-Participante: uno a muchos bidireccional
   // Aportación es el lado de muchos
   @JsonIgnore
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "participante_id")
    private Participante participante;
    // Getters y setters de participante
    public void setParticipante(Participante participante) {
        this.participante = participante;
    }

    public Participante getParticipante() {
        return participante;
    }


    //Blindaje en la entidad
    @PrePersist @PreUpdate
    private void ensureNotNulls() {
        if (this.remitente == null) this.remitente = "";
        if (this.mensaje == null) this.mensaje = "";
    }
}
