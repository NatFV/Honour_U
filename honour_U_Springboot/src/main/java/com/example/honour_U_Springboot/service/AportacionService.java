package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.dto.AportacionDTO;
import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.repository.AportacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Clase Aportacion Service
 * Contiene la lógica de negocio para la clase Aportacion
 */
@Service
public class AportacionService {
    @Autowired
// Inyectamos la instancia de AportaciónRepository usando la anotación @Autowired
    private AportacionRepository aportacionRepository;


    /**
     * Guarda o actualiza una aportación en la base de datos.
     * Si es nueva, asigna automáticamente el orden al final del proyecto.
     * Se fuerza el guardado inmediato con saveAndFlush() para asegurar que
     * los campos generados (como ID o URL) se persisten correctamente.
     */
    public Aportacion saveAportacion(Aportacion aportacion) {
        if (aportacion.getAportacionId() == null && aportacion.getProyecto() != null) {
            // Si es nueva, calcula el orden máximo actual y lo incrementa
            Integer max = aportacionRepository.maxOrdenByProyecto(aportacion.getProyecto());
            aportacion.setOrden((max == null ? 0 : max) + 1);
        }

        //  Guarda y fuerza flush inmediato (para que se persistan todos los cambios)
        return aportacionRepository.saveAndFlush(aportacion);
    }



    /**
     * Método para obtener la aportación por su ID
     *
     * @param id
     * @return aportación
     */
    public Aportacion findAportacionById(Long id) throws Exception {
        return aportacionRepository.findById(id)
                .orElseThrow(() -> new Exception("Aportacion not found with id " + id));
    }

    /**
     * Método para obtener todas las aportaciones
     * @return una lista con todos las aportaciones
     */
    public List<Aportacion> findAllAportaciones(){
        return aportacionRepository.findAll();
    }

    /**
     * Método para actualizar una aportación en la base de datos
     * @param aportacion
     * @return aportación actualizada
     */
    public Aportacion updateAportacion (Long id, Aportacion aportacion){

        Aportacion existente = aportacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Solo actualizamos los campos que pueden cambiar
        existente.setMensaje(aportacion.getMensaje());
        existente.setRemitente(aportacion.getRemitente());
        existente.setUrl(aportacion.getUrl());
        existente.setMediaType(aportacion.getMediaType());
        existente.setEsVisible(aportacion.isEsVisible());

        return aportacionRepository.save(existente); // Esto actualiza, no crea nuevo
    }

    /**
     * Método para eliminar un proyecto por su ID
     * @param id
     */
    public void deleteAportacionById (Long id){
        aportacionRepository.deleteById(id);
    }

    /**
     * Método para encontrar las aportaciones según el formato
     * @param mediaType
     * @return el formato
     */
    public List<Aportacion>aportacionesFormatoVideo(Aportacion.MediaType mediaType){
        return aportacionRepository.findByMediaType(mediaType);
    }


    /**
     * Método para crear una nueva aportación desde un DTO
     * @param aportacionDTO
     * @return una nueva aportación
     */
    public AportacionDTO createAportacionFromDTO(AportacionDTO aportacionDTO) {
        Aportacion aportacion = new Aportacion();
        aportacion.setMensaje(aportacionDTO.getMensaje());
        aportacion.setRemitente(aportacionDTO.getRemitente());
        aportacion.setUrl(aportacionDTO.getUrl());
        aportacion.setMediaType(aportacionDTO.getMediaType());
        aportacion.setEsVisible(aportacionDTO.isEsVisible());
        Aportacion guardada = aportacionRepository.save(aportacion);
        return new AportacionDTO(guardada);
    }

    /**
     * Método para obtener todas las aportaciones como DTO
     * @return lista de todas las aportaciones DTO
     */
    public List<AportacionDTO> getAllAportacionesDTO() {
        return aportacionRepository.findAll().stream()
                .map(AportacionDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Método para obtener una aportación por ID
     * @param id que corresponde a la aportación que se quiere buscar
     * @return la aportación a encontrar
     */
    public AportacionDTO findAportacionByIdAPI(Long id) {
        Aportacion aportacion = aportacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aportación no encontrada"));
        return new AportacionDTO(aportacion); // Convertir la entidad a DTO
    }

    /**
     * Método para actualizar aportación
     * @param id de la aportación que se quiere actualizar
     * @param updatedAportacionDTO
     * @return aportación actualizada
     */
    public AportacionDTO updateAportacion(Long id, AportacionDTO updatedAportacionDTO) {
        Aportacion aportacion = aportacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aportación no encontrada"));

        // Actualizar la entidad con los datos del DTO
        aportacion.setMensaje(updatedAportacionDTO.getMensaje());
        aportacion.setRemitente(updatedAportacionDTO.getRemitente());
        aportacion.setUrl(updatedAportacionDTO.getUrl());
        aportacion.setMediaType(updatedAportacionDTO.getMediaType());
        aportacion.setEsVisible(updatedAportacionDTO.isEsVisible());

        Aportacion updatedAportacion = aportacionRepository.save(aportacion);
        return new AportacionDTO(updatedAportacion);
    }

    /**
     * Método para eliminar una aportación
     * @param id de la aportación
     */
    public void deleteAportacionByIdAPI(Long id) {
        Aportacion aportacion = aportacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aportación no encontrada"));
        aportacionRepository.delete(aportacion);
    }

    /**
     * Método para encontrar aportaciones de formato vídeo
     * @return una lista de aportaciones con formato vídeo
     */
    public List<Aportacion> aportacionesFormatoVideo() {
        return aportacionRepository.findByMediaType(Aportacion.MediaType.VIDEO);
    }
    /**
     * Método para encontrar un proyecto por ID
     * @return una lista de aportaciones del proyecto
     */
    public List<Aportacion> findByProyectoId(Long proyectoId) {
        return aportacionRepository.findByProyecto_ProyectoId(proyectoId);
    }


    /**
     * Método para encontrar un proyecto
     * @return de aportaciones del proyecto
     */
    public List<Aportacion> findByProyecto(Proyecto proyecto) {
        return aportacionRepository.findByProyecto(proyecto);
    }


    // Devuelve las páginas ordenadas (para ver/índice/PDF)
    public List<Aportacion> findByProyectoOrderado(Proyecto p) {
        return aportacionRepository.findByProyectoOrderByOrdenAscAportacionIdAsc(p);
    }

    @Transactional
    public void createOrReplaceSpecialPage(Proyecto p, Aportacion.PageType type) {
        // elimina la/s existente/s de ese tipo
        List<Aportacion> existentes = aportacionRepository.findByProyectoAndPageType(p, type);
        existentes.forEach(aportacionRepository::delete);

        // crea nueva
        Aportacion a = new Aportacion();
        a.setProyecto(p);
        a.setPageType(type);
        a.setEsVisible(true);
        a.setRemitente(""); // no aplica para portada/indice
        a.setMensaje(type == Aportacion.PageType.PORTADA ? "Título del homenaje" : "Índice");

        Integer maxOrden = aportacionRepository.findMaxOrdenByProyecto(p.getProyectoId());
        a.setOrden((maxOrden == null ? 0 : maxOrden) + 1); // al final, simple
        aportacionRepository.save(a);
    }

    @Transactional
    public void changePageTypeEnforcingUniqueness(Proyecto p, Long aportacionId, Aportacion.PageType tipo) {
        Aportacion a = aportacionRepository.findById(aportacionId)
                .orElseThrow(() -> new IllegalArgumentException("Aportación no encontrada"));

        if (!a.getProyecto().getProyectoId().equals(p.getProyectoId())) {
            throw new IllegalArgumentException("No pertenece a este proyecto");
        }

        // Si se convierte a PORTADA o INDICE, eliminar otras del mismo tipo
        if (tipo == Aportacion.PageType.PORTADA || tipo == Aportacion.PageType.INDICE) {
            List<Aportacion> otros = aportacionRepository.findByProyectoAndPageType(p, tipo);
            for (Aportacion o : otros) {
                if (!o.getAportacionId().equals(a.getAportacionId())) {
                    aportacionRepository.delete(o);
                }
            }
            a.setEsVisible(true); // especiales siempre visibles
        }

        a.setPageType(tipo);
        aportacionRepository.save(a);
    }

    @Transactional
    public Aportacion aniadirPaginaBlanco(Proyecto p) {
        Aportacion a = new Aportacion();
        a.setProyecto(p);
        a.setPageType(Aportacion.PageType.BLANCA);
        a.setEsVisible(true);
        a.setRemitente("");
        a.setMensaje("");
        return aportacionRepository.save(a);
    }

    /**
     * Actualiza la PORTADA: cambia el título (mensaje) y opcionalmente sube imagen.
     * Devuelve la portada resultante.
     */
    @Transactional
    public Aportacion updatePortada(Proyecto p, String nuevoTitulo, String imageUrlIfAny) {
        // si no hay portada, créala
        List<Aportacion> portadas = aportacionRepository.findByProyectoAndPageType(p, Aportacion.PageType.PORTADA);
        Aportacion portada = portadas.isEmpty() ? null : portadas.get(0);
        if (portada == null) {
            portada = new Aportacion();
            portada.setProyecto(p);
            portada.setPageType(Aportacion.PageType.PORTADA);
            portada.setEsVisible(true);
            Integer max = aportacionRepository.findMaxOrdenByProyecto(p.getProyectoId());
            portada.setOrden((max == null ? 0 : max) + 1);
        }
        portada.setMensaje(nuevoTitulo != null ? nuevoTitulo : portada.getMensaje());
        if (imageUrlIfAny != null && !imageUrlIfAny.isBlank()) {
            List<String> urls = portada.getMediaUrls();
            if (urls == null) urls = new java.util.ArrayList<>();
            urls.clear();            // 1 imagen para portada (opcional)
            urls.add(imageUrlIfAny);
            portada.setMediaUrls(urls);
        }
        return aportacionRepository.save(portada);
    }

}



