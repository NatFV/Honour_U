package com.example.honour_U_Springboot.controller;

import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.service.DestinatarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController //Maneja solicitudes http
@RequestMapping("/api/destinatarios")//ruta base para los puntos finales
public class DestinatarioController {
    @Autowired
    DestinatarioService destinatarioService;
    //Crear un nuevo destinatario
    @PostMapping
    // ResponseEntity represents the whole HTTP response: status code, headers, and body
    public ResponseEntity<Destinatario> createDestinatario (@RequestBody Destinatario destinatario) {
        Destinatario newDestinatario =destinatarioService.saveDestinatario(destinatario);
        return ResponseEntity.ok(newDestinatario);
    }

    //Obtener todas los destinatarios
    @GetMapping
    public ResponseEntity<List<Destinatario>>getallDestinatarios(){
        List<Destinatario>destinatarios = destinatarioService.findAllDestinatarios();
        return ResponseEntity.ok(destinatarios);
    }

    //Obtener un destinatario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Destinatario>> getDestinatarioById(@PathVariable Long id) throws Exception {
        Optional<Destinatario> destinatario = null;
        try {
            destinatario = destinatarioService.findDestinatarioById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(destinatario);
    }

    //Actualizar libro por ID
    @PutMapping("/{id}")
    public ResponseEntity<Destinatario>updateDestinatario(@PathVariable Long id,
                                                          @RequestBody Destinatario updatedDestinatario ){
        Destinatario destinatario = destinatarioService.updateDestinatario(id, updatedDestinatario);
        return ResponseEntity.ok(destinatario);
    }

    //Eliminar un libro por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteDestinatario(@PathVariable Long id){
        destinatarioService.deleteDestinatarioById(id);
        return ResponseEntity.ok().build();
    }
}
