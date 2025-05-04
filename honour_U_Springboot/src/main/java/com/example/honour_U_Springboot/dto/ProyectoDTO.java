package com.example.honour_U_Springboot.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.model.Libro;

public class ProyectoDTO {

    private Long proyectoId;
    private String nombreProyecto;
    private String organizador;
    private String descripcion;
    private String urlProyecto;
    private LocalDate plazoFinalizacion;


    public ProyectoDTO(Long proyectoId, String nombreProyecto, String organizador, String descripcion, String urlProyecto, LocalDate plazoFinalizacion, Set<AportacionDTO> aportaciones, LibroDTO libro) {
        this.proyectoId = proyectoId;
        this.nombreProyecto = nombreProyecto;
        this.organizador = organizador;
        this.descripcion = descripcion;
        this.urlProyecto = urlProyecto;
        this.plazoFinalizacion = plazoFinalizacion;

    }

    public Long getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Long proyectoId) {
        this.proyectoId = proyectoId;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlProyecto() {
        return urlProyecto;
    }

    public void setUrlProyecto(String urlProyecto) {
        this.urlProyecto = urlProyecto;
    }

    public LocalDate getPlazoFinalizacion() {
        return plazoFinalizacion;
    }

    public void setPlazoFinalizacion(LocalDate plazoFinalizacion) {
        this.plazoFinalizacion = plazoFinalizacion;
    }


}


