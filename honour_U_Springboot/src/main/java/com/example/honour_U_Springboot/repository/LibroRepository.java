package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Public interfaz Libro Repository
 * Realiza una consulta SQL para obtener una lista de libros organizados por país
 * Busca libro asociado a proyecto
 * @author Natalia Fernández
 * @version 1
 */
public interface LibroRepository extends JpaRepository<Libro, Long> {

    /**
     * Consulta para buscar libro por país
     * @param pais
     * @return libro enviado a ese país
     */
    @Query("SELECT DISTINCT l FROM Libro l JOIN l.destinatario d JOIN d.direcciones dir WHERE dir.pais = :pais")
    List<Libro> findLibrosByPais(@Param("pais") String pais);

    /**
     * Método para encontrar un libro asociado a un proyecto
     * @param proyecto
     * @return libro si lo encuentra (o null)
     */
    Optional<Libro> findByProyecto(Proyecto proyecto);
}


