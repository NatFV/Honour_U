package com.example.honour_U_Springboot.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.model.Libro;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Clase Proyecto DTO utilizada para las consultas API
 */
public class ProyectoDTO {

    private Long proyectoId;
    private String nombreProyecto;
    private String organizador;
    private String descripcion;
    private String urlProyecto;
    private LocalDate plazoFinalizacion;
    private Set<AportacionDTO> aportaciones;

    //Constructor sin parámetros:
    @JsonCreator
    public ProyectoDTO() {
        // Constructor vacío necesario para la deserialización con @RequestBody
    }

    //Constructor
    public ProyectoDTO(Proyecto proyecto) {
        this.proyectoId = proyecto.getProyectoId();
        this.nombreProyecto = proyecto.getNombreProyecto();
        this.organizador = proyecto.getOrganizador();
        this.descripcion = proyecto.getDescripcion();
        this.urlProyecto = proyecto.getUrlProyecto();
        this.plazoFinalizacion = proyecto.getPlazoFinalizacion();

        //Añadimos la relación con Aportación
        if (proyecto.getAportaciones() != null) {
            this.aportaciones = proyecto.getAportaciones()
                    .stream()
                    .map(AportacionDTO::new)
                    .collect(Collectors.toSet());
        }

    }

    //Getters y setters


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

    public Set<AportacionDTO> getAportaciones() {
        return aportaciones;
    }

    public void setAportaciones(Set<AportacionDTO> aportaciones) {
        this.aportaciones = aportaciones;
    }

    /**
     * Método toEntity
     * Copia los valores de los atributos del DTO al nuevo objeto Proyecto
     * Si el dto tiene una lista de objetos AportacionDTO los convierte a obejtos Aportacion
     * y los agrega a Proyecto
     * @return un nuevo objeto Proyecto
     */
    public Proyecto toEntity() {
        Proyecto proyecto = new Proyecto();
        proyecto.setProyectoId(this.proyectoId);
        proyecto.setNombreProyecto(this.nombreProyecto);
        proyecto.setOrganizador(this.organizador);
        proyecto.setDescripcion(this.descripcion);
        proyecto.setUrlProyecto(this.urlProyecto);
        proyecto.setPlazoFinalizacion(this.plazoFinalizacion);

        // Convertimos cada AportacionDTO en una Aportacion y la añadimos
        if (this.aportaciones != null) {
            Set<Aportacion> aportacionesSet = this.aportaciones
                    .stream()
                    .map(AportacionDTO::toEntity)  // Convertimos AportacionDTO a Aportacion
                    .collect(Collectors.toSet());
            proyecto.setAportaciones(aportacionesSet);
        }

        return proyecto;
    }

}


