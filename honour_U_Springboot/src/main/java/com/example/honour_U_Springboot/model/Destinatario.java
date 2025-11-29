package com.example.honour_U_Springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Clase destinatario,
 * Representa el destinatario al que se le envía el libro
 * @author Natalia Fernández
 * @version 1
 */
@Entity
@Table(name = "destinatario")
//@Data No funciona
@NoArgsConstructor //Genera constructor sin parámetros para Hibernate
public class Destinatario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "destinatario_id")
    private Long destinatarioId;
    @Column (name = "nombre", nullable = false)
    private String nombre;
    @Column (name = "apellido", nullable = false)
    private String apellido;
    @Column (name = "telefono", nullable = false)
    private String telefono;
    @Column (name = "email", nullable = false)
    private String email;

    //Getters y setters
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

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    //toString
    @Override
    public String toString() {
        return "Destinatario{" +
                "destinatarioId=" + destinatarioId +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", libro=" + libro +
                '}';
    }

    //Relación libro-destinatario 1:1 bidireccional
    //Destinatario es el lado propietario porque lleva la fk
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "fk_libro_id", referencedColumnName = "libro_id")
    private Libro libro;

    //Relación destinatario-dirección 1 a muchos bidireccional
    @JsonIgnore //evita la recursión infinita
    @OneToMany (mappedBy = "destinatario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Direccion> direcciones = new HashSet<>();
    //Getters y setters de direcciones
    public Set<Direccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(Set<Direccion> direcciones) {
        this.direcciones = direcciones;
    }
}

