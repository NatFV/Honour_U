package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.dto.DestinatarioDTO;
import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.repository.DestinatarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DestinatarioService {
    @Autowired
    DestinatarioRepository destinatarioRepository;

    /**
     * Método para guardar el destinatario en la base de datos.
     * @param destinatario
     * @return
     */
    public Destinatario saveDestinatario (Destinatario destinatario){
        //Llamamos al metodo save() de DestinatarioRepository, que guarda el destinatario
        //en la base de datos
        return destinatarioRepository.save(destinatario);
    }

    /**
     * Método para obtener el destinatario por su ID
     * @param id
     * @return destinatario
     */
    public Destinatario findDestinatarioById(Long id)throws Exception {
        return destinatarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Proyecto not found with id " + id));
    }


    /**
     * Método para obtener todos los destinatarios
     * @return una lista con todos los libros
     */
    public List<Destinatario> findAllDestinatarios(){
        return destinatarioRepository.findAll();
    }

    /**
     * Método para actualizar un destinatario en la base de datos
     * @param destinatario
     * @return libro actualizado
     */
    public Destinatario updateDestinatario (Long id, Destinatario destinatario) {

        Destinatario existente = destinatarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Destinatario no encontrado"));

        // Solo actualizamos los campos que pueden cambiar
        existente.setNombre(destinatario.getNombre());
        existente.setApellido(destinatario.getApellido());
        existente.setTelefono(destinatario.getTelefono());
        existente.setEmail(destinatario.getEmail());


        return destinatarioRepository.save(existente);
    }

        /**
         * Método para eliminar un destinatario por su ID
         * @param id
         */
    public void deleteDestinatarioById (Long id){
        destinatarioRepository.deleteById(id);
    }


    /**
     * Método para encontrar los destinatarios con dos direcciones
     * @return una lista de destinatarios con dos direcciones
     */
    Destinatario DestinatariosConDosDirecciones(){
        return destinatarioRepository.findDestinatarioByDireccionesEqualsTwo();
    }


    // Crear un nuevo destinatario desde un DTO
    public DestinatarioDTO createDestinatarioFromDTO(DestinatarioDTO destinatarioDTO) {
        Destinatario destinatario = new Destinatario();
        // Asignar valores de DTO a la entidad
        destinatario.setNombre(destinatarioDTO.getNombre());
        destinatario.setEmail(destinatarioDTO.getEmail());
        destinatario.setTelefono(destinatarioDTO.getTelefono());

        Destinatario savedDestinatario = destinatarioRepository.save(destinatario);
        return new DestinatarioDTO(savedDestinatario);
    }

    // Obtener todos los destinatarios como DTO
    public List<DestinatarioDTO> getAllDestinatariosDTO() {
        return destinatarioRepository.findAll().stream()
                .map(DestinatarioDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener un destinatario por ID
    public DestinatarioDTO findDestinatarioByIdAPI(Long id) {
        Destinatario destinatario = destinatarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
        return new DestinatarioDTO(destinatario);
    }

    // Actualizar un destinatario
    public DestinatarioDTO updateDestinatarioAPI(Long id, DestinatarioDTO updatedDestinatarioDTO) {
        Destinatario destinatario = destinatarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        // Actualizar los valores de la entidad con los del DTO
        destinatario.setNombre(updatedDestinatarioDTO.getNombre());
        destinatario.setEmail(updatedDestinatarioDTO.getEmail());
        destinatario.setTelefono(updatedDestinatarioDTO.getTelefono());

        Destinatario updatedDestinatario = destinatarioRepository.save(destinatario);
        return new DestinatarioDTO(updatedDestinatario);
    }

    // Eliminar un destinatario por ID
    public void deleteDestinatarioByIdAPI(Long id) {
        Destinatario destinatario = destinatarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
        destinatarioRepository.delete(destinatario);
    }
}



