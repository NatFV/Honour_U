package com.example.honour_U_Springboot.controller;
import com.example.honour_U_Springboot.model.Participante;
import com.example.honour_U_Springboot.service.ParticipanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController //Maneja solicitudes http
@RequestMapping("/api/participantes")//ruta base para los puntos finales
public class ParticipanteController {
    @Autowired
    private ParticipanteService participanteService;
    //Crear una nueva aportación
    @PostMapping
    // ResponseEntity represents the whole HTTP response: status code, headers, and body
    public ResponseEntity<Participante> createParticipante (@RequestBody Participante participante) {
        Participante newParticipante =participanteService.saveParticipante(participante);
        return ResponseEntity.ok(newParticipante);
    }

    //Obtener todas las aportaciones
    @GetMapping
    public ResponseEntity<List<Participante>>getallParticipantes(){
        List<Participante>participantes = participanteService.findAllParticipantes();
        return ResponseEntity.ok(participantes);
    }

    //Obtener una aportación por ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Participante>> getParticipanteById(@PathVariable Long id) throws Exception {
        Optional<Participante> participante = null;
        try {
            participante = participanteService.findParticipanteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(participante);
    }

    //Actualizar una aportación por ID
    @PutMapping("/{id}")
    public ResponseEntity<Participante>updateParticipante(@PathVariable Long id,
                                                          @RequestBody Participante updatedParticipante){
        Participante participante = participanteService.updateParticipante(id, updatedParticipante);
        return ResponseEntity.ok(participante);
    }

    //Eliminar una aportación por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteParticipante(@PathVariable Long id){
        participanteService.deleteParticipanteById(id);
        return ResponseEntity.ok().build();
    }

}
