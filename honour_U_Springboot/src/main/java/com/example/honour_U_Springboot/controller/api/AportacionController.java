package com.example.honour_U_Springboot.controller.api;
import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.dto.AportacionDTO;
import com.example.honour_U_Springboot.service.AportacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Clase AportacionController, gestiona las peticiones HTTP que llegan desde la interfaz de usuario
 * y se comunica con la capa de servicio para obtener o modificar los datos.
 * @author Natalia Fernández
 * @version 1
 */

@RestController //Hace que los métodos devuelvan JSON directamente
@RequestMapping("/api/aportaciones") // Ruta base para los puntos finales de las Aportaciones
public class AportacionController {

    @Autowired
    private AportacionService aportacionService; // Servicio que manejará la lógica de negocio para Aportaciones

    /**
     * Método que creauna nueva aportación
     * @param aportacionDTO
     * @return un objeto de tipo AportacionDTO
     */
    @PostMapping
    public ResponseEntity<AportacionDTO> createAportacion(@RequestBody AportacionDTO aportacionDTO) {
        try {
            AportacionDTO creado = aportacionService.createAportacionFromDTO(aportacionDTO);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para obtener todas las aportaciones
     * @return una lista de todas las aportaciones
     */
    @GetMapping
    public ResponseEntity<List<AportacionDTO>> findAllAportaciones() {
        try {
            List<AportacionDTO> aportacionesDTO = aportacionService.getAllAportacionesDTO();
            System.out.println("Aportaciones obtenidas: " + aportacionesDTO);

            if (aportacionesDTO.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(aportacionesDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para encontrar una aportación específica
     * @param id, que sirve como parámetro de búsqueda
     * @return la aportación buscada o una excepción
     */
    @GetMapping("/{id}")
    public ResponseEntity<AportacionDTO> findAportacionByIdAPI(@PathVariable Long id) {
        try {
            AportacionDTO aportacionDTO = aportacionService.findAportacionByIdAPI(id);
            if (aportacionDTO == null) {
                return ResponseEntity.notFound().build(); // Si no se encuentra, retornar 404
            }
            return ResponseEntity.ok(aportacionDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para actualizar una aportación
     * @param id que sirve para buscar la aportación con un id específico
     * @param updatedAportacionDTO es la información a actualizar
     * @return un objeto de tipo AportacionDTO actualizado y un código de estado 200OK que indica
     *   que la operación fue exitosa
     */
    @PutMapping("/{id}")
    public ResponseEntity<AportacionDTO> updateAportacionAPI(@PathVariable Long id, @RequestBody AportacionDTO updatedAportacionDTO) {
        try {
            AportacionDTO aportacionDTO = aportacionService.updateAportacion(id, updatedAportacionDTO);
            return ResponseEntity.ok(aportacionDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Eliminar una aportación por ID
     * @param id para encontrar aportación
     * @return un código de estado que indica si se pudo eliminar
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAportacion(@PathVariable Long id) {
        try {
            aportacionService.deleteAportacionById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para encontrar las aportaciones de formato vídeo
     * @return una lista de aportaciones de formato vídeo
     */
    @GetMapping("/videos")
    public ResponseEntity<List<Aportacion>> getAportacionesFormatoVideo() {
        List<Aportacion> aportaciones = aportacionService.aportacionesFormatoVideo();

        if (aportaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(aportaciones);
    }
}

