package com.example.honour_U_Springboot.repository;
import com.example.honour_U_Springboot.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ParticipanteRepository extends JpaRepository<Participante,Long> {
        @Query("SELECT a.participante FROM Aportacion a GROUP BY a.participante ORDER BY COUNT(a.aportacionId) DESC")
        Participante findParticipanteByMasAportaciones();

        @Query("SELECT DISTINCT p FROM Participante p JOIN p.aportaciones a WHERE a.proyecto.proyectoId = :proyectoId")
        List<Participante> findByProyectoId(@Param("proyectoId") Long proyectoId);
    }

