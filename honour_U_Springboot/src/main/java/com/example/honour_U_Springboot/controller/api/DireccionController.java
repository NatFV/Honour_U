package com.example.honour_U_Springboot.controller.api;

import com.example.honour_U_Springboot.dto.DireccionDTO;
import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Direccion;
import com.example.honour_U_Springboot.repository.DestinatarioRepository;
import com.example.honour_U_Springboot.repository.DireccionRepository;
import com.example.honour_U_Springboot.service.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Clase DireccionController, gestiona las peticiones HTTP que llegan desde la interfaz de usuario
 * y se comunica con la capa de servicio para obtener o modificar los datos.
 * @author Natalia Fernández
 * @version 1
 */
@RestController //Hace que los métodos devuelvan JSON directamente
@RequestMapping("/api/direcciones") // Ruta base para Direcciones
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    @Autowired
    private DestinatarioRepository destinatarioRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    /**
     * Método para crear una dirección
     * @param direccionDTO con la información de la dirección
     * @return mensaje para corroborar que se creó
     */
    @PostMapping
    public ResponseEntity<DireccionDTO> createDireccion(@RequestBody DireccionDTO direccionDTO) {
        try {
            DireccionDTO creada = direccionService.createDireccionFromDTO(direccionDTO);
            return new ResponseEntity<>(creada, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para obtener todas las direcciones
     * @return una lista con todas las direcciones
     */
    @GetMapping
    public ResponseEntity<List<DireccionDTO>> findAllDirecciones() {
        try {
            List<DireccionDTO> direcciones = direccionService.getAllDireccionesDTO();
            if (direcciones.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(direcciones);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para obtener dirección por id
     * @param id
     * @return direccioón encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<DireccionDTO> findDireccionByIdAPI(@PathVariable Long id) {
        try {
            DireccionDTO direccionDTO = direccionService.findDireccionByIdAPI(id);
            if (direccionDTO == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(direccionDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para actualizar dirección
     * @param id de la dirección que se quiere actualizar
     * @param updatedDTO información de la dirección
     * @return mensaje que corrobora que se actualizó
     */
    @PutMapping("/{id}")
    public ResponseEntity<DireccionDTO> updateDireccionAPI(@PathVariable Long id, @RequestBody DireccionDTO updatedDTO) {
        try {
            DireccionDTO direccionDTO = direccionService.updateDireccionAPI(id, updatedDTO);
            return ResponseEntity.ok(direccionDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para eliminar dirección
     * @param id de la dirección que se quiere eliminar
     * @return mensaje que corrobora que se eliminó
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDireccion(@PathVariable Long id) {
        try {
            direccionService.deleteDireccionById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Método para guardar dirección
     * @param direccion
     * @param destinatarioId
     * @return
     */
    @PostMapping("/api/direcciones/nueva")
    public String guardarDireccion(@ModelAttribute Direccion direccion,
                                   @RequestParam Long destinatarioId) {

        // 1. Obtener destinatario por id
        Destinatario destinatario = destinatarioRepository.findById(destinatarioId)
                .orElseThrow(() -> new IllegalArgumentException("Destinatario no encontrado"));

        // 2. Asignar destinatario a la dirección
        direccion.setDestinatario(destinatario);

        // 3. Añadir dirección al conjunto de direcciones del destinatario (para mantener la relación sincronizada)
        destinatario.getDirecciones().add(direccion);

        // 4. Guardar la dirección (que es el lado propietario)
        direccionRepository.save(direccion);

        return "redirect:/direcciones";
    }

    /**
     * Método para obtener las direcciones internacionales
     * @return una lista de direcciones internacionales
     */
    @GetMapping("/internacionales")
    public ResponseEntity<List<Direccion>> getDireccionesInternacionales() {
        List<Direccion> direcciones = direccionService.obtenerDireccionesInternacionales();
        if (direcciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(direcciones);
    }
}
