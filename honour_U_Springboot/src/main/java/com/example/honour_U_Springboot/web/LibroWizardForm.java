// src/main/java/com/example/honour_U_Springboot/web/form/LibroWizardForm.java
package com.example.honour_U_Springboot.web.form;

import jakarta.validation.constraints.*;

public class LibroWizardForm {

    // Libro
    @NotBlank private String tituloLibro;
    @NotBlank private String formato; // PDF, Tapa blanda, etc.
    @Min(1) private int copias = 1;
    @Min(1) private int paginas = 1;

    // Nuevos flags (checkboxes)
    private boolean envioEmail;   // ☐ Enviar por email
    private boolean envioFisico;  // ☐ Crear libro físico

    // Email (si envioEmail)
    @Email private String email;

    // Físico (si envioFisico)
    private String destinatarioNombre;
    private String destinatarioApellido;
    private String telefono;

    private String calle;
    private String piso;
    private String letra;
    private String codigoPostal;
    private String pais;

    // getters & setters...
    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
    public int getCopias() { return copias; }
    public void setCopias(int copias) { this.copias = copias; }
    public int getPaginas() { return paginas; }
    public void setPaginas(int paginas) { this.paginas = paginas; }
    public boolean isEnvioEmail() { return envioEmail; }
    public void setEnvioEmail(boolean envioEmail) { this.envioEmail = envioEmail; }
    public boolean isEnvioFisico() { return envioFisico; }
    public void setEnvioFisico(boolean envioFisico) { this.envioFisico = envioFisico; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDestinatarioNombre() { return destinatarioNombre; }
    public void setDestinatarioNombre(String destinatarioNombre) { this.destinatarioNombre = destinatarioNombre; }
    public String getDestinatarioApellido() { return destinatarioApellido; }
    public void setDestinatarioApellido(String destinatarioApellido) { this.destinatarioApellido = destinatarioApellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }
    public String getPiso() { return piso; }
    public void setPiso(String piso) { this.piso = piso; }
    public String getLetra() { return letra; }
    public void setLetra(String letra) { this.letra = letra; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}
