package com.example.honour_U_Springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase Libro
 * Representa los libros generados por proyectos
 * @author uthor Natalia Fernández
 * @version 1
 */
@Entity
@Table(name = "libro")
//@Data omitido ya que genera problemas (no funciona la notación correctamente)
@NoArgsConstructor //Genera constructor sin parámetros para Hibernate
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="libro_id")
    private Long libroId;
    @Column(name = "titulo_libro",nullable = false )
    private String tituloLibro;
    private String formato;
    private int copias;
    private int paginas;

    //Getters y setters

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public int getCopias() {
        return copias;
    }

    public void setCopias(int copias) {
        this.copias = copias;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    //toString
    @Override
    public String toString() {
        return "Libro{" +
                "libroId=" + libroId +
                ", tituloLibro='" + tituloLibro + '\'' +
                ", formato='" + formato + '\'' +
                ", copias=" + copias +
                ", paginas=" + paginas +
                ", proyecto=" + proyecto +
                '}';
    }

    //Relación con Proyecto 1:1 bidireccional. Libro es el lado propietario porque contiene la FK
    @OneToOne
    @JoinColumn(name = "fk_proyecto_id", referencedColumnName = "proyecto_id")
    private Proyecto proyecto;

    //Relación con Destinatario 1:1 bidireccional.
    //La relación propietaria es el lado del destinatario porque contiene la FK
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "libro", orphanRemoval = true)
    private Destinatario destinatario;
    //Getters y setters de destinatario
    public Destinatario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Destinatario destinatario) {
        this.destinatario = destinatario;
    }
}

