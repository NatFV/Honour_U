package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.dto.ParticipanteDTO;
import com.example.honour_U_Springboot.model.Participante;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.repository.ParticipanteRepository;
import com.example.honour_U_Springboot.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParticipanteService {
    // Inyectamos la instancia de ParticipanteRepository usando la anotación @Autowired
    @Autowired
    private ParticipanteRepository participanteRepository;
    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    public ParticipanteService(ParticipanteRepository participanteRepository, ProyectoRepository proyectoRepository) {
        this.participanteRepository = participanteRepository;
        this.proyectoRepository = proyectoRepository;
    }

    /**
     * Método para guardar la aportación en la base de datos.
     * @param participante
     * @return
     */
    public Participante saveParticipante (Participante participante){
        if (participante.getProyecto() != null) {
            Long proyectoId = participante.getProyecto().getProyectoId();
            if (proyectoId != null) {
                Proyecto proyectoPersistido = proyectoRepository.findById(proyectoId)
                        .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id " + proyectoId));
                participante.setProyecto(proyectoPersistido); // asigna el proyecto persistido
            } else {
                // Aquí, el proyecto es transient y no tiene ID. Debes guardarlo primero o lanzar error.
                throw new RuntimeException("Proyecto debe tener un ID válido para asignarse a Participante");
            }
        }
        return participanteRepository.save(participante);
    }

    /**
     * Método para obtener el participante por su ID
     * @param id
     * @return participante
     */
    public Participante findParticipanteById(Long id)throws Exception {
        return participanteRepository.findById(id)
                .orElseThrow(() -> new Exception("Aportacion not found with id " + id));
    }

    /**
     * Método para obtener todos los participantes
     * @return una lista con todos los participantes
     */
    public List<Participante> findAllParticipantes(){
        return participanteRepository.findAll();
    }

    /**
     * Método para actualizar un participante en la base de datos
     * @param participante
     * @return participante actualizado
     */
    public Participante updateParticipante (Long id, Participante participante) {

        Participante existente = participanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participante no encontrado"));

        // Solo actualizamos los campos que pueden cambiar
        existente.setNombre(participante.getNombre());
        existente.setApellido(participante.getApellido());

        existente.setEmail(participante.getEmail());


        return participanteRepository.save(existente); // Esto actualiza, no crea nuevo
    }

    /**
     * Método para eliminar un participante por su ID
     * @param id
     */
    public void deleteParticipanteById (Long id){
        participanteRepository.deleteById(id);
    }

    /**
     * Método que encuentra el participante que ha hecho más aportaciones
     * @return el participante que ha realizado más aportaciones
     */
    public List<Participante> participantesConMasAportaciones() {
        return participanteRepository.findParticipantesConMasAportaciones();
    }


    public List<ParticipanteDTO> findAllParticipanteDTOs() {
        return participanteRepository.findAll().stream().map(p -> {
            ParticipanteDTO dto = new ParticipanteDTO();
            dto.setId(p.getParticipanteId());
            dto.setNombre(p.getNombre());
            dto.setApellido(p.getApellido());
            dto.setEmail(p.getEmail());
            dto.setAportacionesMensajes(
                    p.getAportaciones().stream()
                            .map(a -> a.getMensaje())
                            .collect(Collectors.toList())
            );
            return dto;
        }).collect(Collectors.toList());
    }

    public List<Participante> findAll() {
        return participanteRepository.findAll();
    }

    public List<Participante> findByProyecto(Proyecto proyecto) {
        return participanteRepository.findByProyectoId(proyecto.getProyectoId());
    }

    /**
     * Méodo para encontrar el proyecto del participante
     * @param proyectoId
     * @return la lista de participantes con ese proyecto
     */
    public List<Participante> findByProyectoId(Long proyectoId) {
        return participanteRepository.findByProyectoProyectoId(proyectoId);
    }



}

