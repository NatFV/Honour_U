package com.example.honour_U_Springboot.controller.api;
import com.example.honour_U_Springboot.dto.ParticipanteDTO;
import com.example.honour_U_Springboot.model.Participante;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.repository.ProyectoRepository;
import com.example.honour_U_Springboot.service.ParticipanteService;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Clase ParticipanteController, usada para las consultas API
 */
@RestController //Maneja solicitudes http
@RequestMapping("/api/participantes")//ruta base para los puntos finales
public class ParticipanteController {
    @Autowired
    private ParticipanteService participanteService;
    @Autowired
    private ProyectoRepository proyectoRepository;

    /**
     * Método para crear un nuevo participante
     * @param dto los datos para crearlos
     * @return mensaje para verificar que se agregó el participante
     */
    @PostMapping
    public ResponseEntity<?> createParticipante(@RequestBody ParticipanteDTO dto) {
        if (dto.getProyectoId() == null) {
            return ResponseEntity.badRequest().body("El proyectoId no puede ser nulo");
        }

        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID " + dto.getProyectoId()));

        Participante participante = new Participante();
        participante.setNombre(dto.getNombre());
        participante.setApellido(dto.getApellido());
        participante.setEmail(dto.getEmail());
        participante.setProyecto(proyecto);

        Participante saved = participanteService.saveParticipante(participante);
        return ResponseEntity.ok(saved);
    }


    /**
     * Método para obtener todos los participantes
     * @return una lista con todos los participantes
     */
    @GetMapping
    public ResponseEntity<List<Participante>>getallParticipantes(){
        List<Participante>participantes = participanteService.findAllParticipantes();
        return ResponseEntity.ok(participantes);
    }

    /**
     * Método para obtener un participante por id
     * @param id del participante que se desea encontrar
     * @return participante encontrado
     * @throws Exception si no se encuentra participante
     */
    @GetMapping("/{id}")
    public ResponseEntity<Participante> getParticipanteById(@PathVariable Long id) throws Exception {
        Participante participante = null;
        try {
            participante = participanteService.findParticipanteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(participante);
    }


    /**
     * Método para actualizar un participante por id
     * @param id del participante que se desea actualizar
     * @param updatedParticipante datos del participante actualizado
     * @return mensaje que confirma la actualización
     */
    @PutMapping("/{id}")
    public ResponseEntity<Participante>updateParticipante(@PathVariable Long id,
                                                          @RequestBody Participante updatedParticipante){
        Participante participante = participanteService.updateParticipante(id, updatedParticipante);
        return ResponseEntity.ok(participante);
    }

    /**
     * Método para eliminar participante
     * @param id con el participante a eliminar
     * @return mensaje de ok para verificar eliminación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteParticipante(@PathVariable Long id){
        participanteService.deleteParticipanteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Método para encontrar el participante con más aportaciones
     * @return el participante con más aportaciones.
     */
    @GetMapping("/mas-aportaciones")
    public ResponseEntity<List<Participante>> getParticipantesConMasAportaciones() {
        List<Participante> participantes = participanteService.participantesConMasAportaciones();
        if (participantes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(participantes);
    }

}
