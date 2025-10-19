package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Public interfaz Proyecto Repository
 * Extiende de JpaARepository
 * Realiza una consulta SQL devolver el proyecto con más aportaciones
 */

public interface ProyectoRepository extends JpaRepository <Proyecto, Long> {


    @Query(value = "SELECT p.* FROM proyecto p " +
            "JOIN aportacion a ON a.proyecto_id = p.proyecto_id " +
            "GROUP BY p.proyecto_id " +
            "ORDER BY COUNT(a.aportacion_id) DESC " +
            "LIMIT 1", nativeQuery = true)
    Proyecto findProyectoByMaxAportaciones();

    /**
     * Método para encontrar el proyecto por URL
     * @param tokenUrl
     * @return
     */
    Optional<Proyecto> findByTokenUrl(String tokenUrl);
    Optional<Proyecto> findByAdminToken(String adminToken);
}

