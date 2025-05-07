package com.example.honour_U_Springboot.controller;

import com.example.honour_U_Springboot.dto.DireccionDTO;
import com.example.honour_U_Springboot.service.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones") // Ruta base para Direcciones
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    // Crear una nueva dirección
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

    // Obtener todas las direcciones
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

    // Obtener dirección por ID
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

    // Actualizar dirección
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

    // Eliminar dirección
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
}
