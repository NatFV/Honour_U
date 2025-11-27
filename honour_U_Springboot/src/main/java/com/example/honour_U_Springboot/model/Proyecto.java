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
 * @author Natalia Fdez
 * @version 1
 */

@Entity //la clase es una entidad y será mapeada en tabla
//@Data No funciona la notación, he tenido que generar getters y setters manualmente
@Table(name = "proyecto")
public class Proyecto {
    @Id //Esta anotación indica que el atributo id es la clave primaria de la entidad
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Sindica que el valor de la clave primaria de una entidad se generará automáticamente por la base de datos
    @Column(name="proyecto_id")
    private Long proyectoId;
    @Column(name= "nombre_proyecto", nullable = false)
    private String nombreProyecto;
    @Column(name= "organizador", nullable = false)
    private String organizador;
    private String descripcion;

    //Token único que se genera automáticamente para las aportaciones de proyecto
    @Column(name = "token_url", unique = true, nullable = false)
    private String tokenUrl;

    //Token para que el organizador tenga acceso al panel de control
    @Column(name = "admin_token", unique = true)
    private String adminToken;

    //URL generada a partir del token para los participantes
    @Column(name= "url_proyecto", unique = true, nullable = false)
    private String urlProyecto;
    //URL generada a partir del adminToken para el administrador
    @Column (name= "url_admin", unique = true, nullable = false)
    private String urlAdmin;

    @Column(name="plazo_finalizacion", nullable = false)
    private LocalDate plazoFinalizacion;

   //Constructores
    public Proyecto() {
    }

    public Proyecto(String nombreProyecto, String organizador, String descripcion, LocalDate plazoFinalizacion) {
        this.nombreProyecto = nombreProyecto;
        this.organizador = organizador;
        this.descripcion = descripcion;
        this.plazoFinalizacion = plazoFinalizacion;
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

    public String getUrlAdmin() {
        return urlAdmin;
    }

    public void setUrlAdmin(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    //toString

    @Override
    public String toString() {
        return "Proyecto{" +
                "proyectoId=" + proyectoId +
                ", nombreProyecto='" + nombreProyecto + '\'' +
                ", organizador='" + organizador + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", tokenUrl='" + tokenUrl + '\'' +
                ", urlProyecto='" + urlProyecto + '\'' +
                ", adminToken='" + adminToken + '\'' +
                ", plazoFinalizacion=" + plazoFinalizacion +
                ", urlAdmin='" + urlAdmin + '\'' +
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
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "proyecto", orphanRemoval = true)//es proyecto
    private Libro libro;

    //Getters y setters de libro
    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }


    /**
     * Método para generar tokens públicos y de administrador de panel de control, necesarios para generar URLS
     * Si no existe un token para aportaciones o un token de administrador,
     * se crea un Universally Unique Identifier (UUID), que usa números aleatorios con probabilidad
     * muy baja para repetirse (128bits).
     */
    @PrePersist //se asegura de que antes de guardar el proyecto en la base de datos, ejecute este método
    public void ensureTokens() {
        if (this.adminToken == null || this.adminToken.isBlank()) {
            this.adminToken = java.util.UUID.randomUUID().toString();
        }
        if (this.tokenUrl == null || this.tokenUrl.isBlank()) {
            this.tokenUrl = java.util.UUID.randomUUID().toString();
        }
    }
}

