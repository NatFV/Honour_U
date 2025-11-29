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
 * Proporciona métodos de consulta relacionados con:
 * Búsqueda por proyecto o por atributos concretos
 * Obtención del orden máximo de aportaciones dentro de un proyecto
 * Consultas para portadas e índices
 * @author Natalia Fernandez
 * @version 1
 */
public interface AportacionRepository extends JpaRepository<Aportacion, Long> {

    /** BÚSQUEDAS BÁSICAS */
    /**
     * Obtiene todas las aportaciones cuyo tipo de media coincide con el indicado
     * @param mediaType (imagen, vídeo, etc)
     * @return lista de aportaciones del tipo indicado
     */
    List<Aportacion> findByMediaType(Aportacion.MediaType mediaType);

    /**
     * Método que obtiene las aportaciones asociadas a un proyecto por su ID
     * @param proyectoId
     * @return la lista de aportaciones del proyecto
     */

    List<Aportacion> findByProyecto_ProyectoId(Long proyectoId);

    /**
     * Método para obtener las aportaciones asociadas a un proyecto concreto
     * @param proyecto entidad proyecto
     * @return lista de aportaciones de ese proyecto
     */
    List<Aportacion> findByProyecto(Proyecto proyecto);


    /**CONSULTAS RELACIONADAS CON EL ORDEN**/
    /**
     * Consulta que obtiene el valor máxico del campo "orden" dentro de un proyecto
     * Coloca la aportación en último lugar
     * Sirve para definir el orden visual de las aportaciones del proyecto
     * @param proyecto al que pertenecen las aportaciones
     * @return máximo valor del orden (es el último elemento) o 0 si no existen
     */
    @Query("select coalesce(max(a.orden), 0) from Aportacion a where a.proyecto = :proyecto")
    Integer maxOrdenByProyecto(@Param("proyecto") Proyecto proyecto);

    /**
     * Consulta que obitene el máximo valor del campo orden de un proyecto a partir de su ID
     * Coloca la aportación en último lugar
     * Sirve para definir el orden visual de las aportaciones del proyecto
     * @param proyectoId identificador del proyecto
     * @return máximo valor (es el último elemento)  o 0 si no hay aportaciones
     */
    @Query("select max(a.orden) from Aportacion a where a.proyecto.proyectoId = :proyectoId")
    Integer findMaxOrdenByProyecto(@Param("proyectoId") Long proyectoId);


    /**CONSULTAS RELACIONADAS CON PORTADAS E INDICES**/
    /**
     * Método que obtiene las aportaciones del proyecto filtradas por tipo de página
     * Sirve para definir el orden visual de las aportaciones del proyecto
     * @param proyecto al que pertenecen las aportaciones
     * @param pageType tipo de página
     * @return lista de aportaciones coincidentes
     */
    List<Aportacion> findByProyectoAndPageType(Proyecto proyecto, Aportacion.PageType pageType);

    /**
     * Método que obtiene todas las aportacoines de proyecto ordenadas de forma estable
     * @param proyecto al que pertenecen las aportaciones
     * @return lista ordenada de aportaciones
     */
    List<Aportacion> findByProyectoOrderByOrdenAscAportacionIdAsc(Proyecto proyecto);



}


