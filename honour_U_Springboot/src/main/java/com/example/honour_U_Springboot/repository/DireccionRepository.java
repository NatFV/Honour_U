package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByPaisNot(String pais);

    //Método para contar los libros por país que se utilizará en LibroService
    @Query("SELECT d.pais, COUNT(l) FROM Direccion d JOIN d.destinatario des JOIN des.libro l GROUP BY d.pais")
    List<Object[]> countLibrosPorPais();


}
