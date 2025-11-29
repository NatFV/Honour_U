package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Public interfaz Proyecto Repository
 * Realiza una consulta SQL devolver el proyecto con más aportaciones
 * Consta de dos métodos para encontrar los tokens que se usan para crear URLs
 * @author Natalia Fernández
 * @version 1
 *
 */

public interface ProyectoRepository extends JpaRepository <Proyecto, Long> {

    /**
     * Consulta para obtener el mayor número de aportaciones
     * Realiza un join entre proyecto y aportación
     * Agrupa por ID de proyecto
     * Ordena de mayor a menor número de aportaciones
     * @return el primer proyecto.
     */

    @Query(value = "SELECT p.* FROM proyecto p " +
            "JOIN aportacion a ON a.proyecto_id = p.proyecto_id " +
            "GROUP BY p.proyecto_id " +
            "ORDER BY COUNT(a.aportacion_id) DESC " +
            "LIMIT 1", nativeQuery = true)
    Proyecto findProyectoByMaxAportaciones();

    /**
     * Método para encontrar el token para construir el link de aportaciones
     * @param tokenUrl
     * @return Optional que contiene el proyecto si existe
     */
    Optional<Proyecto> findByTokenUrl(String tokenUrl);

    /**
     * Método para encontrar el token que se utiliza para construir el link del
     * panel de control para el administrador
     * @param adminToken
     * @return Optional que contiene el proyecto si existe
     */
    Optional<Proyecto> findByAdminToken(String adminToken);
}

