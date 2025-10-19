package com.example.honour_U_Springboot.service;


import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.dto.ProyectoDTO;
import com.example.honour_U_Springboot.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Clase ProyectoService
 * Contiene la lógica de negocio para la clase Proyecto
 */
@Service
public class ProyectoService {

    @Autowired
    // Inyectamos la instancia de ProyectoRepository usando la anotación @Autowired
    private ProyectoRepository proyectoRepository;

    /**
     * Método saveProyecto, para guardar el proyecto en la base de datos y genera un token único si no existe,
     * a través de un Identificador Universalmente Único (UUID).
     * @param proyecto
     * @return proyecto salvado
     */
    public Proyecto saveProyecto (Proyecto proyecto){
        // Genera un token único solo si no existe
        if (proyecto.getTokenUrl() == null || proyecto.getTokenUrl().isEmpty()) {
            proyecto.setTokenUrl(UUID.randomUUID().toString());
        }
        //Llamamos al metodo save() de ProyectoRepository, que guarda el proyecto
        //en la base de datos
        return proyectoRepository.save(proyecto);
    }

    /**
     * Método para buscar un proyecto por token
     * @param tokenUrl es la url con identificador único que se utiliza para localizar el proyecto
     * @return el proyecto encontrado si lo encuentra
     */
    public Proyecto findByTokenUrl(String tokenUrl) {
        return proyectoRepository.findByTokenUrl(tokenUrl).orElse(null);
    }



    /**
     * Método findProyectoById, para obtener un proyecto por su ID
     * @param id
     * @return proyecto con el id correspondiente
     */
    public Proyecto findProyectoById(Long id) throws Exception {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new Exception("Proyecto not found with id " + id));
    }

    /**
     * Método findAllProyectos, para obtener todos los proyectos
     * @return una lista con todos los proyectos
     */
    public List<Proyecto> findAllProyectos(){
       return proyectoRepository.findAll();
    }

    /**
     * Método para actualizar un proyecto en la base de datos
     * @param proyecto que se desea actualizar
     * @return proyecto actualizado
     */
    public Proyecto updateProyecto (Long id, Proyecto proyecto){

        Proyecto existente = proyectoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Solo actualizamos los campos que pueden cambiar
        existente.setNombreProyecto(proyecto.getNombreProyecto());
        existente.setOrganizador(proyecto.getOrganizador());
        existente.setDescripcion(proyecto.getDescripcion());
        existente.setUrlProyecto(proyecto.getUrlProyecto());
        existente.setPlazoFinalizacion(proyecto.getPlazoFinalizacion());

        return proyectoRepository.save(existente); // Esto actualiza, no crea nuevo
    }


    /**
     * Método para eliminar un proyecto por su ID
     * @param id para identificar el proyecto a eliminar
     */
    public void deleteProyectoById (Long id){
        proyectoRepository.deleteById(id);
    }

    /**
     * Método para encontrar el proyecto con más aportaciones
     * @return proyecto con más aportaciones
     */
    public Proyecto findProyectoByMaxAportaciones(){
        return proyectoRepository.findProyectoByMaxAportaciones();
    }

    /**
     * Método para crear proyecto a partir del DTO
     */
    public ProyectoDTO createProyectoFromDTO(ProyectoDTO proyectoDTO) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombreProyecto(proyectoDTO.getNombreProyecto());
        proyecto.setOrganizador(proyectoDTO.getOrganizador());
        proyecto.setDescripcion(proyectoDTO.getDescripcion());
        proyecto.setUrlProyecto(proyectoDTO.getUrlProyecto());
        proyecto.setPlazoFinalizacion(proyectoDTO.getPlazoFinalizacion());

        // Genera adminToken si no viene
        if (proyecto.getAdminToken() == null || proyecto.getAdminToken().isBlank()) {
            proyecto.setAdminToken(java.util.UUID.randomUUID().toString());
        }

        // (si también quieres generar tokenUrl aquí, haz algo parecido)
        // if (proyecto.getTokenUrl() == null || proyecto.getTokenUrl().isBlank()) {
        //     proyecto.setTokenUrl(java.util.UUID.randomUUID().toString());
        // }


        Proyecto guardado = proyectoRepository.save(proyecto);
        return new ProyectoDTO(guardado);
    }

    /**
     * Método para mostrar todos los proyectos
     */
    public List<ProyectoDTO>getAllProyectosDTO(){
        List<ProyectoDTO> proyectos = proyectoRepository.findAll().stream()
                .map(ProyectoDTO::new)
                .collect(Collectors.toList());
        return proyectos != null ? proyectos : new ArrayList<>();
    }

    /**
     * Método para encontrar los proyectos por ID
     * @param id con el id del proyecto a buscar
     * @return proyecto encontrado
     * @throws Exception si no encuentra el proyecto
     */
    public ProyectoDTO findProyectoByIdAPI(Long id) throws Exception {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new Exception("Proyecto no encontrado"));

        return new ProyectoDTO(proyecto); // Suponiendo que el constructor de ProyectoDTO toma un Proyecto
    }

    /**
     * Método para actualizar el proyecto
     * @param id del proycto que se quiere actualizar
     * @param updatedProyectoDTO datos del proyecto a actualizar
     * @return un proyecto actualizado para la API
     */
    public ProyectoDTO updateProyectoAPI(Long id, ProyectoDTO updatedProyectoDTO) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Convertir ProyectoDTO a Proyecto
        proyecto.setNombreProyecto(updatedProyectoDTO.getNombreProyecto());
        // Aquí debes mapear todos los campos necesarios desde ProyectoDTO a Proyecto

        Proyecto updatedProyecto = proyectoRepository.save(proyecto);

        return new ProyectoDTO(updatedProyecto); // Retornar el ProyectoDTO actualizado
    }

    /**
     * Método findAll para encontrar todos los proyectos
     * @return una lista con todos los proyectos
     */
    public List<Proyecto> findAll() {
        return proyectoRepository.findAll();
    }

    /**
     * Método para encontrar los proyectos por ID
     * @param id del proyecto a encontrar
     * @return el proyecto encontrado
     */
    public Proyecto findById(Long id) {
        return proyectoRepository.findById(id).orElse(null);
    }


    //Método para buscar por adminToken
    public Proyecto findByAdminToken(String adminToken) throws Exception {
        return proyectoRepository.findByAdminToken(adminToken)
                .orElseThrow(() -> new Exception("Proyecto no encontrado (adminToken)"));
    }


}

