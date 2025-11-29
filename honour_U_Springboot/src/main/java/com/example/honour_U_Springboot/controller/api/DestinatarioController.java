package com.example.honour_U_Springboot.controller.api;

import com.example.honour_U_Springboot.dto.DestinatarioDTO;
import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.service.DestinatarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Clase DestinatarioController, gestiona las peticiones HTTP que llegan desde la interfaz de usuario
 * y se comunica con la capa de servicio para obtener o modificar los datos.
 * @author Natalia Fernández
 * @version 1
 */
@RestController //Hace que los métodos devuelvan JSON directamente
@RequestMapping("/api/destinatarios") // Ruta base para los puntos finales de los Destinatarios
public class DestinatarioController {

    @Autowired
    private DestinatarioService destinatarioService; // Servicio que manejará la lógica de negocio para Destinatarios

    // Crear un nuevo destinatario
    @PostMapping
    public ResponseEntity<DestinatarioDTO> createDestinatario(@RequestBody DestinatarioDTO destinatarioDTO) {
        try {
            DestinatarioDTO creado = destinatarioService.createDestinatarioFromDTO(destinatarioDTO);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Obtener todos los destinatarios
    @GetMapping
    public ResponseEntity<List<DestinatarioDTO>> findAllDestinatarios() {
        try {
            List<DestinatarioDTO> destinatariosDTO = destinatarioService.getAllDestinatariosDTO();

            if (destinatariosDTO.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(destinatariosDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    /**
     * Método para obtener destinatarios con dos direcciones
     * @return
     */
    @GetMapping("/filtro/dos-direcciones")
    public ResponseEntity<Destinatario> getDestinatarioConDosDirecciones() {
        Destinatario destinatario = destinatarioService.destinatarioConDosDirecciones();
        if (destinatario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(destinatario);
    }
    // Obtener un destinatario por ID
    @GetMapping("/{id}")
    public ResponseEntity<DestinatarioDTO> findDestinatarioByIdAPI(@PathVariable Long id) {
        try {
            DestinatarioDTO destinatarioDTO = destinatarioService.findDestinatarioByIdAPI(id);
            if (destinatarioDTO == null) {
                return ResponseEntity.notFound().build(); // Si no se encuentra, retornar 404
            }
            return ResponseEntity.ok(destinatarioDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Actualizar un destinatario por ID
    @PutMapping("/{id}")
    public ResponseEntity<DestinatarioDTO> updateDestinatarioAPI(@PathVariable Long id, @RequestBody DestinatarioDTO updatedDestinatarioDTO) {
        try {
            DestinatarioDTO destinatarioDTO = destinatarioService.updateDestinatarioAPI(id, updatedDestinatarioDTO);
            return ResponseEntity.ok(destinatarioDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Eliminar un destinatario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestinatario(@PathVariable Long id) {
        try {
            destinatarioService.deleteDestinatarioById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
