// ParticipanteDTO.java
package com.example.honour_U_Springboot.dto;

import java.util.List;

public class ParticipanteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private List<String> aportacionesMensajes;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getAportacionesMensajes() {
        return aportacionesMensajes;
    }

    public void setAportacionesMensajes(List<String> aportacionesMensajes) {
        this.aportacionesMensajes = aportacionesMensajes;
    }
}
