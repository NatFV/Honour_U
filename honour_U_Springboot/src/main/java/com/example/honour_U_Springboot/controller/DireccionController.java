package com.example.honour_U_Springboot.controller;

import com.example.honour_U_Springboot.model.Direccion;
import com.example.honour_U_Springboot.service.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController //Maneja solicitudes http
@RequestMapping("/api/direcciones")//ruta base para los puntos finales
public class DireccionController {

    @Autowired
    DireccionService direccionService;
    //Crear una nueva dirección
    @PostMapping
    // ResponseEntity represents the whole HTTP response: status code, headers, and body
    public ResponseEntity<Direccion> createDireccion (@RequestBody Direccion direccion) {
        Direccion newDireccion = direccionService.saveDireccion(direccion);
        return ResponseEntity.ok(newDireccion);
    }

    //Obtener todas las direcciones
    @GetMapping
    public ResponseEntity<List<Direccion>>getallDirecciones(){
        List<Direccion>direcciones = direccionService.findAllDirecciones();
        return ResponseEntity.ok(direcciones);
    }

    //Obtener una dirección por ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Direccion>> getDireccionById(@PathVariable Long id) throws Exception {
        Optional<Direccion> direccion = null;
        try {
            direccion = direccionService.findDireccionById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(direccion);
    }

    //Actualizar dirección por ID
    @PutMapping("/{id}")
    public ResponseEntity<Direccion>updateDireccion(@PathVariable Long id,
                                                    @RequestBody Direccion updatedDireccion ){
        Direccion direccion =direccionService.updateDireccion(id, updatedDireccion);
        return ResponseEntity.ok(direccion);
    }

    //Eliminar una dirección por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteDireccion(@PathVariable Long id){
        direccionService.deleteDireccionById(id);
        return ResponseEntity.ok().build();
    }
}
