
package com.example.honour_U_Springboot.dto;

import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Direccion;
import com.example.honour_U_Springboot.model.Libro;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Clase DestinatarioDTO utilizada para las consultas API
 * @author Natalia Fernández
 * @version 1
 */
public class DestinatarioDTO {

    private Long destinatarioId;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private LibroDTO libro;  // Relación con LibroDTO
    private Set<DireccionDTO> direcciones = new HashSet<>(); // Relación con DireccionDTO

    //Constructor sin parámetros
    public DestinatarioDTO() {
        // Constructor vacío necesario para Jackson
    }

    // Constructor para inicializar el DTO a partir de la entidad Destinatario
    public DestinatarioDTO(Destinatario destinatario) {
        this.destinatarioId = destinatario.getDestinatarioId();
        this.nombre = destinatario.getNombre();
        this.apellido = destinatario.getApellido();
        this.telefono = destinatario.getTelefono();
        this.email = destinatario.getEmail();

        // Relación con Libro: si existe un libro asociado, lo convertimos a DTO
        if (destinatario.getLibro() != null) {
            this.libro = new LibroDTO(destinatario.getLibro());
        }

        // Relación con Direccion: si existen direcciones, las convertimos a DTO
        if (destinatario.getDirecciones() != null) {
            this.direcciones = destinatario.getDirecciones()
                    .stream()
                    .map(DireccionDTO::new)
                    .collect(Collectors.toSet());
        }
    }

    // Métodos Getters y Setters

    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LibroDTO getLibro() {
        return libro;
    }

    public void setLibro(LibroDTO libro) {
        this.libro = libro;
    }

    public Set<DireccionDTO> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(Set<DireccionDTO> direcciones) {
        this.direcciones = direcciones;
    }
}

