package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Destinatario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
/**
 * Public interfaz Destinatario Repository
 * Extiende de JpaARepository
 * Realiza una consulta SQL devolver el destinatario con dos direcciones
 * @author Natalia Fernández
 * @version 1
 */
public interface DestinatarioRepository extends JpaRepository<Destinatario, Long> {
    /**
     * Consulta para encontrar destinatarios con 2 direcciones
     * @return el destinatario con dos direcciones si existe
     */
    @Query("SELECT d FROM Destinatario d " +
            "JOIN d.direcciones dir " +
            "GROUP BY d " +
            "HAVING COUNT(dir) = 2")
    Optional<Destinatario> findDestinatarioByDireccionesEqualsTwo();


}
