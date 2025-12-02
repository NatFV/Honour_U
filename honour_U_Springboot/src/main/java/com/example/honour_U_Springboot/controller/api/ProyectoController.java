package com.example.honour_U_Springboot.controller.api;

import com.example.honour_U_Springboot.dto.ProyectoDTO;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Clase ProyectoController, gestiona las peticiones HTTP que llegan desde la interfaz de usuario
 * y se comunica con la capa de servicio para obtener o modificar los datos.
 * @author Natalia Fernández
 * @version 1
 */
@RestController //Hace que los métodos devuelvan JSON directamente
@RequestMapping("/api/proyectos")//ruta base para los puntos finales
public class ProyectoController {
    @Autowired //para inyección de dependencias
    private ProyectoService proyectoService;

    /**
     * Creación de un nuevo proyecto a partir de la clase ProyectoDTO
     * @param proyectoDTO, es la clase que se proporciona para la creación del proyecto
     * @return un objeto de tipo ProyectoDTO
     */
    @PostMapping
    // ResponseEntity represents the whole HTTP response: status code, headers, and body
    public ResponseEntity<ProyectoDTO> createProyectoFromDTO (@RequestBody ProyectoDTO proyectoDTO) {
        ProyectoDTO creado = proyectoService.createProyectoFromDTO(proyectoDTO);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    /**
     * Método que muestra todos los proyectos
     * @return una lista de todos los proyectos presentes en la base de datos
     */
    @GetMapping
    public ResponseEntity<List<ProyectoDTO>> findAllProyectosDTO() {
        try {
            List<ProyectoDTO> proyectosDTO = proyectoService.getAllProyectosDTO();
            System.out.println("Proyectos obtenidos: " + proyectosDTO);

            if (proyectosDTO.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(proyectosDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * Método para buscar y obtener un proyecto a partir de su id
     * @param id, es el id que se proporciona para la búsqueda
     * @return un objeto correspondiente al proyecto con el id solicitado
     * @throws Exception si no encuentra el proyecto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProyectoDTO> findProyectoByIdAPI(@PathVariable Long id) throws Exception {
        ProyectoDTO proyectoDTO = null;
        try {
            proyectoDTO = proyectoService.findProyectoByIdAPI(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(proyectoDTO);
    }

    /**
     * Método para actualizar un proyecto en función de su ID
     * @param id, que se proporciona para encontrar el proyecto a actualizar
     * @param updatedProyectoDTO información para actualizar
     * @return un objeto de tipo ProyectoDTO actualizado y un código de estado 200OK que indica
     * que la operación fue exitosa
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProyectoDTO> updateProyectoAPI(@PathVariable Long id,
                                                      @RequestBody ProyectoDTO updatedProyectoDTO) {
        ProyectoDTO proyectoDTO = proyectoService.updateProyectoAPI(id, updatedProyectoDTO);
        return ResponseEntity.ok(proyectoDTO);
    }

    /**
     * Método para eliminar un proyecto dado su id
     * @param id, para encontrar el proyecto
     * @return un código de estado HTTP 200 OK que indica que la actualización fue exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteProyecto(@PathVariable Long id){
        proyectoService.deleteProyectoById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Método que busca el proyecto con más aportaciones
     * @return un objeto de tipo Proyecto DTO con las aportaciones
     */
    @GetMapping("/max-aportaciones")
    public ResponseEntity<ProyectoDTO> getProyectoConMasAportaciones() {
        try {
            Proyecto proyecto = proyectoService.findProyectoByMaxAportaciones();
            if (proyecto == null) {
                return ResponseEntity.notFound().build();
            }
            ProyectoDTO dto = new ProyectoDTO(proyecto);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
