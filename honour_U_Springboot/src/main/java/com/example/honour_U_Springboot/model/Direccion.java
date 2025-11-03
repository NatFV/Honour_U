package com.example.honour_U_Springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase direcciones
 * Representa las direcciones a las que se envía el libro
 */
@Entity
@Table(name = "direccion")
//@Data
@NoArgsConstructor //Genera constructor sin parámetros para Hibernate
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="direccion_id")
    private Long direccionId;
    @Column(name="calle", nullable = false)
    private String calle;

    @Column(name="número", nullable = false)
    private String numero;
    @Column(name="piso")
    private String piso;
    @Column(name="letra")
    private String letra;
    @Column(name = "codigo_postal")
    private String codigoPostal;
    private String pais;

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

    public String getNumero() { return numero; }

    public void setNumero(String numero) { this.numero = numero;}

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

    public Destinatario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Destinatario destinatario) {
        this.destinatario = destinatario;
    }

    @Override
    public String toString() {
        return "Direccion{" +
                "direccionId=" + direccionId +
                ", calle='" + calle + '\'' +
                ", piso='" + piso + '\'' +
                ", letra='" + letra + '\'' +
                ", codigoPostal='" + codigoPostal + '\'' +
                ", pais='" + pais + '\'' +
                ", destinatario=" + destinatario +
                '}';
    }

    //Relación destinatario-dirección: bidireccional de uno a muchos
    //Dirección (Lado "Muchos")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id")
    private Destinatario destinatario;


}

