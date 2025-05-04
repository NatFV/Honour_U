package com.example.honour_U_Springboot.repository;
import com.example.honour_U_Springboot.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


    public interface ParticipanteRepository extends JpaRepository<Participante,Long> {
        @Query("SELECT a.participante FROM Aportacion a GROUP BY a.participante ORDER BY COUNT(a.aportacionId) DESC")
        Participante findParticipanteByMasAportaciones();
    }

