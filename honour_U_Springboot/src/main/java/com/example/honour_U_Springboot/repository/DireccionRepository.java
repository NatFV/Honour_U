package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
/**
 * Public interfaz Direccion Repository
 * Extiende de JpaARepository
 * Tiene un método para encontrar direcciones que no incluyan a un país determinado
 * Realiza una consulta SQL para contar libros por país.
 * @author Natalia Fernández
 * @version 1
 */
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    /**
     * Método para encontrar direcciones que no incluyan un país determinado
     * @param pais que no ha de incluirse
     * @return lista de direcciones que no incluyen a ese país
     */
    List<Direccion> findByPaisNot(String pais);

    /**
     * Consulta para contar libros por país
     * @return el número de libros por país
     */
    @Query("SELECT d.pais, COUNT(l) FROM Direccion d JOIN d.destinatario des JOIN des.libro l GROUP BY d.pais")
    List<Object[]> countLibrosPorPais();

    /**
     * Método para encontrar las direcciones de un destinatario
     * @param destinatario
     * @return lista de direcciones asociadas a ese destinatario
     */
    List<Direccion> findByDestinatario(Destinatario destinatario);



}
