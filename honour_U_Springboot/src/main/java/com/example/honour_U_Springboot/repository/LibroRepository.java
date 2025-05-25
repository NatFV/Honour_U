package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Public interfaz Libro Repository
 * Extiende de JpaARepository
 * Realiza una consulta SQL para obtener una lista de libros organizados por país
 */
public interface LibroRepository extends JpaRepository<Libro, Long> {
    @Query("SELECT DISTINCT l FROM Libro l JOIN l.destinatario d JOIN d.direcciones dir WHERE dir.pais = :pais")
    List<Libro> findLibrosByPais(@Param("pais") String pais);
}


