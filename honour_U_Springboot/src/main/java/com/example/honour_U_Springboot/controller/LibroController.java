package com.example.honour_U_Springboot.controller;

import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController //Maneja solicitudes http
@RequestMapping("/api/libros")//ruta base para los puntos finales
public class LibroController {
    @Autowired
    LibroService libroService;
    //Crear un nuevo libro
    @PostMapping
    // ResponseEntity represents the whole HTTP response: status code, headers, and body
    public ResponseEntity<Libro> createLibro (@RequestBody Libro libro) {
        Libro newLibro =libroService.saveLibro(libro);
        return ResponseEntity.ok(newLibro);
    }

    //Obtener todas los libros
    @GetMapping
    public ResponseEntity<List<Libro>>getallLibros(){
        List<Libro>libros = libroService.findAllLibros();
        return ResponseEntity.ok(libros);
    }

    //Obtener una aportación por ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Libro>> getLibroById(@PathVariable Long id) throws Exception {
        Optional<Libro> libro = null;
        try {
            libro = libroService.findLibroById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(libro);
    }

    //Actualizar libro por ID
    @PutMapping("/{id}")
    public ResponseEntity<Libro>updateLibro(@PathVariable Long id,
                                            @RequestBody Libro updatedLibro){
        Libro libro = libroService.updateLibro(id,updatedLibro);
        return ResponseEntity.ok(libro);
    }

    //Eliminar un libro por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteLibro(@PathVariable Long id){
        libroService.deleteLibroById(id);
        return ResponseEntity.ok().build();
    }

}

