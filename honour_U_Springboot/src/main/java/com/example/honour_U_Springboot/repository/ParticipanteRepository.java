package com.example.honour_U_Springboot.repository;
import com.example.honour_U_Springboot.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Public interfaz Participante Repository
 * Extiende de JpaARepository
 * Realiza una consulta SQL devolver la lista de participantes con más aportaciones
 * Realiza una consulta SWL para encontrar el proyecto por Id
 * Realiza un método para encontrar el proyecto
 */
public interface ParticipanteRepository extends JpaRepository<Participante,Long> {
    @Query("SELECT p FROM Participante p WHERE size(p.aportaciones) = (SELECT MAX(size(p2.aportaciones)) FROM Participante p2)")
    List<Participante> findParticipantesConMasAportaciones();


    @Query("SELECT DISTINCT p FROM Participante p JOIN p.aportaciones a WHERE a.proyecto.proyectoId = :proyectoId")
        List<Participante> findByProyectoId(@Param("proyectoId") Long proyectoId);

    List<Participante> findByProyectoProyectoId(Long proyectoId);

}



