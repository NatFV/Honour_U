package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.dto.AportacionDTO;
import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.repository.AportacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AportacionService {

    @Autowired
    private AportacionRepository aportacionRepository;

    /** CREACIÓN Y GUARDADO DE APORTACIONES **/
    /**
     * Método para guardar aportación
     * @param aportacion para guardar
     * Si la aportación es nueva se le asigna un orden y ID
     *                  Después se le asigna la URL
     * @return aportación guardada
     */
    public Aportacion saveAportacion(Aportacion aportacion) {

        boolean esNueva = (aportacion.getAportacionId() == null);

        // SOLO si la aportación es nueva, asignamos el orden
        if (esNueva && aportacion.getProyecto() != null) {
            Integer max = aportacionRepository.maxOrdenByProyecto(aportacion.getProyecto());
            aportacion.setOrden((max == null ? 0 : max) + 1);
        }

        // Primer guardado: genera ID si es nueva
        //Se usa saveandflush porque necesitamos el ID inmediatamente
        aportacion = aportacionRepository.saveAndFlush(aportacion);

        // SOLO si es nueva asignamos la URL base (necesita ID) para visualizar aportación completa
        //cuando se necesite.
        if (esNueva) {
            if (aportacion.getUrl() == null || aportacion.getUrl().isBlank()) {
                aportacion.setUrl(
                        "/proyectos/token/" +
                                aportacion.getProyecto().getTokenUrl() +
                                "/aportaciones/" +
                                aportacion.getAportacionId() +
                                "/ver"
                );
            }

            // Guardar de nuevo solo en creación
            aportacion = aportacionRepository.saveAndFlush(aportacion);
        }

        // Si es actualización, no se vuelve a guardar
        return aportacion;
    }

    /**
     * Cración de aportación DTO
     * @param aportacionDTO para su creación
     * @return nueva aportación
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

    /** ACTUALIZACIÓN DE APORTACIONES **/
    /**
     * Actualizar aportación
     * @param id de la aportación
     * @param form formulario de la aportación
     * @return aportación actualizada tras obtener los valores anteriores y editarlos
     */
    public Aportacion updateAportacion(Long id, Aportacion form) {

        Aportacion aport = aportacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aportación no encontrada"));

        aport.setMensaje(form.getMensaje() == null ? "" : form.getMensaje());

        aport.setRemitente(
                (form.getRemitente() == null || form.getRemitente().isBlank())
                        ? "Anónimo"
                        : form.getRemitente()
        );

        aport.setEsVisible(form.isEsVisible());
        aport.setPageType(Aportacion.PageType.NORMAL);

        return aportacionRepository.save(aport);
    }

    /**
     * Método para actualizar aportaciones DTO
     * @param id de la aportación DTO
     * @param updatedAportacionDTO aportación actualizada
     * @return la aportación DTO actualizada
     */
    public AportacionDTO updateAportacion(Long id, AportacionDTO updatedAportacionDTO) {
        Aportacion aportacion = aportacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aportación no encontrada"));

        aportacion.setMensaje(updatedAportacionDTO.getMensaje());
        aportacion.setRemitente(updatedAportacionDTO.getRemitente());
        aportacion.setUrl(updatedAportacionDTO.getUrl());
        aportacion.setMediaType(updatedAportacionDTO.getMediaType());
        aportacion.setEsVisible(updatedAportacionDTO.isEsVisible());

        Aportacion updatedAportacion = aportacionRepository.save(aportacion);
        return new AportacionDTO(updatedAportacion);
    }

    /**ELIMINACIÓN DE APORTACIONES**/

    /**
     * Método para eliminar aportación por id
     * @param id de la aportación
     */
    public void deleteAportacionById(Long id){
        aportacionRepository.deleteById(id);
    }

    /**
     * Método para eliminar aportación DTO
     * @param id de la aportación DTO
     */
    public void deleteAportacionByIdAPI(Long id) {
        Aportacion aportacion = aportacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aportación no encontrada"));
        aportacionRepository.delete(aportacion);
    }
    public Aportacion findAportacionById(Long id) throws Exception {
        return aportacionRepository.findById(id)
                .orElseThrow(() -> new Exception("Aportacion not found with id " + id));
    }

    public List<Aportacion> findAllAportaciones(){
        return aportacionRepository.findAll();
    }

    /** BÚSQUEDAS **/

    /**
     * Método para encontrar aportaciones por proyecto
     * @param proyecto
     * @return lista de aportaciones por proyecto
     */
    public List<Aportacion> findByProyecto(Proyecto proyecto) {
        return aportacionRepository.findByProyecto(proyecto);
    }

    /**
     * Método para encontrar las aportaciones por proyecto ordenadas
     * @param p
     * @return lista de aportaciones ordenadas.
     */
    public List<Aportacion> findByProyectoOrderado(Proyecto p) {
        return aportacionRepository.findByProyectoOrderByOrdenAscAportacionIdAsc(p);
    }


    /**
     * Método para obtener todas las aportaciones DTO
     * @return lista de aportaciones DTO
     */
    public List<AportacionDTO> getAllAportacionesDTO() {
        return aportacionRepository.findAll().stream()
                .map(AportacionDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Método para encontrar aportaciones DTO por id
     * @param id de la aportación DTO
     * @return aportación dto encontrada
     */
    public AportacionDTO findAportacionByIdAPI(Long id) {
        Aportacion aportacion = aportacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aportación no encontrada"));
        return new AportacionDTO(aportacion);
    }


    /**
     * Método para encontrar aportaciones en formato vídeo
     * @return aportaciones de video
     */
    public List<Aportacion> aportacionesFormatoVideo() {
        return aportacionRepository.findByMediaType(Aportacion.MediaType.VIDEO);
    }


    /**
     * Método para añadir páginas en blanco.
     * @param p
     * @return aportación de página en blanco.
     */
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

}
