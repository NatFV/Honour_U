package com.example.honour_U_Springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase Proyecto
 * Esta clase se utiliza para crear un nuevo proyecto que representa un homenaje
 */

@Entity //la clase es una entidad y será mapeada en tabla
//@Data No funciona la notación, he tenido que generar getters y setters manualmente
@Table(name = "proyecto")
@NoArgsConstructor //Genera constructor sin parámetros para Hibernate
public class Proyecto {
    @Id //Esta anotación indica que el atributo id es la clave primaria de la entidad
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Se utiliza con bases de datos que admiten columnas de identidad
    @Column(name="proyecto_id")
    private Long proyectoId;
    @Column(name= "nombre_proyecto", nullable = false)
    private String nombreProyecto;
    @Column(name= "organizador", nullable = false)
    private String organizador;
    private String descripcion;

    //token_url representa un token único que se genera automáticamente
    @Column(name = "token_url", unique = true, nullable = false)
    private String tokenUrl;

    //Generamos un atributo url_proyecto por si lo queremos generar manualmente
    @Column(name= "url_proyecto")
    private String urlProyecto;

    //Generamos un token para el administrador
    @Column(name = "admin_token", unique = true)
    private String adminToken;

    @Column(name="plazo_finalizacion")
    @Temporal(TemporalType.DATE) //Incluimos la fecha pero no la hora
    private LocalDate plazoFinalizacion;

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

    public String getTokenUrl() { return tokenUrl; }

    public void setTokenUrl(String tokenUrl) {this.tokenUrl = tokenUrl;}

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
    public String getAdminToken() { return adminToken; }
    public void setAdminToken(String adminToken) { this.adminToken = adminToken; }



    @Override
    public String toString() {
        return "Proyecto{" +
                "proyectoId=" + proyectoId +
                ", nombreProyecto='" + nombreProyecto + '\'' +
                ", organizador='" + organizador + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", tokenUrl='" + tokenUrl + '\'' +
                ", urlProyecto='" + urlProyecto + '\'' +
                ", plazoFinalizacion=" + plazoFinalizacion +

                '}';
    }

    //Relación Proyecto-Aportación de uno a muchos bidireccional
    //orphanRemoval: elimina automaticamente de la colección los hijos borrados
    //fetch= lazy: carga las aportaciones sólo cuando se necesitan
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Aportacion> aportaciones = new HashSet<>();
    //Getters y setters de aportaciones
    public Set<Aportacion> getAportaciones() {
        return aportaciones;
    }
    public void setAportaciones(Set<Aportacion> aportaciones) {
        this.aportaciones = aportaciones;
    }

    //Relación Proyecto-Libro es de uno a uno bidireccional. El lado propietario es Libro
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "proyecto")//es proyecto
    private Libro libro;
    //Getters y setters de libro
    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    /**
     * Método para generar los link de aportaciones y de administrador
     */
    @PrePersist
    public void ensureTokens() {
        if (this.adminToken == null || this.adminToken.isBlank()) {
            this.adminToken = java.util.UUID.randomUUID().toString();
        }
        if (this.tokenUrl == null || this.tokenUrl.isBlank()) {
            this.tokenUrl = java.util.UUID.randomUUID().toString();
        }
    }
}

