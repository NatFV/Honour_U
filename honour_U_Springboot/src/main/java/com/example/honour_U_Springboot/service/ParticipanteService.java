package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.model.Participante;
import com.example.honour_U_Springboot.repository.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/participantes")
public class ParticipanteService {
    // Inyectamos la instancia de ParticipanteRepository usando la anotación @Autowired
    @Autowired
    private ParticipanteRepository participanteRepository;

    /**
     * Método para guardar la aportación en la base de datos.
     * @param participante
     * @return
     */
    public Participante saveParticipante (Participante participante){
        //Llamamos al metodo save() de ProyectoRepository, que guarda el proyecto
        //en la base de datos
        return participanteRepository.save(participante);
    }

    /**
     * Método para obtener el participante por su ID
     * @param id
     * @return participante
     */
    public Optional<Participante> findParticipanteById(Long id){
        return participanteRepository.findById(id);
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
    public Participante updateParticipante (Long id, Participante participante){
        return participanteRepository.save(participante);
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
    public Participante participanteConMasAportaciones(){
        return participanteRepository.findParticipanteByMasAportaciones();
    }
}
