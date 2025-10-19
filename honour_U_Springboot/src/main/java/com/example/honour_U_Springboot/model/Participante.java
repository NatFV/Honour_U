package com.example.honour_U_Springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase Participante, representa los participantes del proyecto
 */
@Entity
@Table(name = "participante")
//@Data: nota: he tenido que añadir Getters, Setters y toString manualmente
//No funciona la notación
@NoArgsConstructor //Genera constructor sin parámetros para Hibernate
public class Participante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "participante_id")
    private Long participanteId;
    private String nombre;
    private String apellido;
    private String email;

    public Long getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Long participanteId) {
        this.participanteId = participanteId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Aportacion> getAportaciones() {
        return aportaciones;
    }

    public void setAportaciones(List<Aportacion> aportaciones) {
        this.aportaciones = aportaciones;
    }

    @Override
    public String toString() {
        return "Participante{" +
                "participanteId=" + participanteId +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", aportaciones=" + aportaciones +
                '}';
    }

    //Relación participante uno a muchos bidireccional (participante del lado de uno)
    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore
    //Inicializamos el HashSet para que se puedan añadir aportaciones
    private List<Aportacion> aportaciones = new ArrayList<>();;

    // Método para agregar aportaciones
    public void addAportacion(Aportacion aportacion) {
        aportacion.setParticipante(this);  // Asegura que la relación bidireccional se mantenga
        this.aportaciones.add(aportacion);  // Agrega la aportación a la colección
    }

    // Método para eliminar aportaciones
    public void removeAportacion(Aportacion aportacion) {
        aportacion.setParticipante(null);  // Elimina la referencia al participante
        this.aportaciones.remove(aportacion);  // Elimina la aportación de la colección
    }

    //Relación participante proyecto
    // Relación con Proyecto
    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;

    // Getters y Setters

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }





}

