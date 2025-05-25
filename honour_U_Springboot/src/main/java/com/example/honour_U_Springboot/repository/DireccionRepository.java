package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
/**
 * Public interfaz Direccion Repository
 * Extiende de JpaARepository
 * Tiene un método para encontrar libros que no incluyan a un país determinado
 * Realiza una consulta SQL para contar libros por país.
 */
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByPaisNot(String pais);


    @Query("SELECT d.pais, COUNT(l) FROM Direccion d JOIN d.destinatario des JOIN des.libro l GROUP BY d.pais")
    List<Object[]> countLibrosPorPais();


}
