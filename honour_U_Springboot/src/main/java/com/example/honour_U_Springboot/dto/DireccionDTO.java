package com.example.honour_U_Springboot.dto;

import com.example.honour_U_Springboot.model.Direccion;
import com.example.honour_U_Springboot.model.Destinatario;

public class DireccionDTO {

    private Long direccionId;
    private String calle;
    private String piso;
    private String letra;
    private String codigoPostal;
    private String pais;
    // Evitamos recursividad incluyendo solo el ID del destinatario o nada
    private Long destinatarioId;

    //Constructor para Direccion DTO
    public DireccionDTO() {
        // Constructor vacío necesario para Jackson
    }

    // Constructor para inicializar el DTO a partir de la entidad Direccion
    public DireccionDTO(Direccion direccion) {
        this.direccionId = direccion.getDireccionId();
        this.calle = direccion.getCalle();
        this.piso = direccion.getPiso();
        this.letra = direccion.getLetra();
        this.codigoPostal = direccion.getCodigoPostal();
        this.pais = direccion.getPais();
        this.destinatarioId = direccion.getDestinatario() != null ?
                direccion.getDestinatario().getDestinatarioId() : null;


    }

    // Métodos Getters y Setters
    public Long getDireccionId() {
        return direccionId;
    }

    public void setDireccionId(Long direccionId) {
        this.direccionId = direccionId;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }


}
