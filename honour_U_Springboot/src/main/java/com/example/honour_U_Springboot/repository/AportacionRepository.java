package com.example.honour_U_Springboot.repository;

import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Public interfaz Proyecto Repository
 * Extiende de JpaARepository
 * Consta de tres métodos
 * finByProyecto: encuentra la aportación por proyect
 * findByProyecto_ProyectoId: encuentra la aportación por proyecto id
 * findByMediaType: encuentra las aportaciones en formato vídeo
 */
public interface AportacionRepository extends JpaRepository<Aportacion, Long> {

    List<Aportacion> findByMediaType(Aportacion.MediaType mediaType);

    List<Aportacion> findByProyecto_ProyectoId(Long proyectoId);

    List<Aportacion> findByProyecto(Proyecto proyecto);

    //Para el administrador
    List<Aportacion> findByProyectoOrderByOrdenAsc(Proyecto proyecto);

    @Query("select coalesce(max(a.orden), 0) from Aportacion a where a.proyecto = :proyecto")
    Integer maxOrdenByProyecto(@Param("proyecto") Proyecto proyecto);

    //Para la creación de las portadas y los índices
    // 1) Buscar por proyecto + tipo (para garantizar unicidad PORTADA/INDICE)
    List<Aportacion> findByProyectoAndPageType(Proyecto proyecto, Aportacion.PageType pageType);

    // 2) Orden estable: por orden y, a igualdad, por id (evita saltos visuales)
    List<Aportacion> findByProyectoOrderByOrdenAscAportacionIdAsc(Proyecto proyecto);

    //Máximo orden por id
    @Query("select max(a.orden) from Aportacion a where a.proyecto.proyectoId = :proyectoId")
    Integer findMaxOrdenByProyecto(@Param("proyectoId") Long proyectoId);

}


