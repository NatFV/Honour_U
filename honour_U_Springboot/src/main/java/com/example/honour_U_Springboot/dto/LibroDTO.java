package com.example.honour_U_Springboot.dto;

import com.example.honour_U_Springboot.model.Libro;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Clase LibroDTO utilizada para las consultas API
 */
public class LibroDTO {

    private Long libroId;
    private String tituloLibro;
    private String formato;
    private int copias;
    private int paginas;
    @JsonIgnore // Evitamos la recursividad al serializar Proyecto
    private ProyectoDTO proyecto; // Usamos ProyectoDTO para evitar el ciclo infinito


    //Constructores
    public LibroDTO() {
        // Constructor vacío si es necesario
    }
    public LibroDTO(Libro libro) {
        this.libroId = libro.getLibroId();
        this.tituloLibro = libro.getTituloLibro();
        this.formato = libro.getFormato();
        this.copias = libro.getCopias();
        this.paginas = libro.getPaginas();

        // Evitar que la relación Proyecto cause recursión
        if (libro.getProyecto() != null) {
            this.proyecto = new ProyectoDTO(libro.getProyecto());
        }
    }
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

    public ProyectoDTO getProyecto() {
        return proyecto;
    }

    public void setProyecto(ProyectoDTO proyecto) {
        this.proyecto = proyecto;
    }
    //toString
    @Override
    public String toString() {
        return "LibroDTO{" +
                "libroId=" + libroId +
                ", tituloLibro='" + tituloLibro + '\'' +
                ", formato='" + formato + '\'' +
                ", copias=" + copias +
                ", paginas=" + paginas +
                ", proyecto=" + proyecto +
                '}';
    }

    /**
     * Método toEntity, convierte atributos del DTO al nuevo objeto Libro
     * @return el nuevo objeto Libro
     */
    public Libro toEntity() {
        Libro libro = new Libro();
        libro.setLibroId(this.libroId);
        libro.setTituloLibro(this.tituloLibro);
        libro.setFormato(this.formato);
        libro.setCopias(this.copias);
        libro.setPaginas(this.paginas);

        // Si el DTO tiene un proyecto, lo convertimos a entidad y lo asignamos
        if (this.proyecto != null) {
            libro.setProyecto(this.proyecto.toEntity()); // Convertir el DTO de Proyecto a entidad Proyecto
        }

        return libro;
    }
}
