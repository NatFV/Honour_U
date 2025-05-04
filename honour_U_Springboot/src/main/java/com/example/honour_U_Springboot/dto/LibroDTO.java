package com.example.honour_U_Springboot.dto;

public class LibroDTO {

    private Long libroId;
    private String tituloLibro;
    private String formato;
    private int copias;
    private int paginas;

    //Constructor
    public LibroDTO(Long libroId, String tituloLibro, String formato, int copias, int paginas) {
        this.libroId = libroId;
        this.tituloLibro = tituloLibro;
        this.formato = formato;
        this.copias = copias;
        this.paginas = paginas;
    }

    //Getters and setters

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
}
