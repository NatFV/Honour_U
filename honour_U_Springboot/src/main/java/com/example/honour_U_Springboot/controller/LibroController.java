package com.example.honour_U_Springboot.controller;

import com.example.honour_U_Springboot.dto.LibroDTO;
import com.example.honour_U_Springboot.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Maneja solicitudes HTTP
@RequestMapping("/api/libros") // Ruta base para los puntos finales de los Libros
public class LibroController {

    @Autowired
    private LibroService libroService; // Servicio que manejará la lógica de negocio para Libros

    // Crear un nuevo libro
    @PostMapping
    public ResponseEntity<LibroDTO> createLibro(@RequestBody LibroDTO libroDTO) {
        try {
            LibroDTO creado = libroService.createLibroFromDTOAPI(libroDTO);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Obtener todos los libros
    @GetMapping
    public ResponseEntity<List<LibroDTO>> findAllLibros() {
        try {
            List<LibroDTO> librosDTO = libroService.getAllLibrosDTOAPI();

            if (librosDTO.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(librosDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Obtener un libro por ID
    @GetMapping("/{id}")
    public ResponseEntity<LibroDTO> findLibroById(@PathVariable Long id) {
        try {
            LibroDTO libroDTO = libroService.findLibroByIdAPI(id);
            if (libroDTO == null) {
                return ResponseEntity.notFound().build(); // Si no se encuentra, retornar 404
            }
            return ResponseEntity.ok(libroDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Actualizar un libro por ID
    @PutMapping("/{id}")
    public ResponseEntity<LibroDTO> updateLibro(@PathVariable Long id, @RequestBody LibroDTO updatedLibroDTO) {
        try {
            LibroDTO libroDTO = libroService.updateLibroAPI(id, updatedLibroDTO);
            return ResponseEntity.ok(libroDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Eliminar un libro por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibro(@PathVariable Long id) {
        try {
            libroService.deleteLibroByIdAPI(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
