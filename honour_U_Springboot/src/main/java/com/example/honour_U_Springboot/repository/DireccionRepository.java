package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByPaisNot(String pais);

}
