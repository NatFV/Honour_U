package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.dto.DestinatarioDTO;
import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Direccion;
import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.repository.DestinatarioRepository;
import com.example.honour_U_Springboot.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DestinatarioService {
    @Autowired
    DestinatarioRepository destinatarioRepository;
    LibroRepository libroRepository;
    public DestinatarioService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }




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



    // Crear un nuevo destinatario desde un DTO
    public DestinatarioDTO createDestinatarioFromDTO(DestinatarioDTO destinatarioDTO) {
        Destinatario destinatario = new Destinatario();

        // Asignamos datos básicos
        destinatario.setNombre(destinatarioDTO.getNombre());
        destinatario.setApellido(destinatarioDTO.getApellido());
        destinatario.setEmail(destinatarioDTO.getEmail());
        destinatario.setTelefono(destinatarioDTO.getTelefono());



        // Creamos las direcciones a partir de los DTOs
        if (destinatarioDTO.getDirecciones() != null && !destinatarioDTO.getDirecciones().isEmpty()) {
            Set<Direccion> direcciones = destinatarioDTO.getDirecciones().stream().map(dtoDir -> {
                Direccion direccion = new Direccion();
                direccion.setCalle(dtoDir.getCalle());
                direccion.setPiso(dtoDir.getPiso());
                direccion.setLetra(dtoDir.getLetra());
                direccion.setCodigoPostal(dtoDir.getCodigoPostal());
                direccion.setPais(dtoDir.getPais());

                // Relación bidireccional: asignar destinatario a la dirección
                direccion.setDestinatario(destinatario);

                return direccion;
            }).collect(Collectors.toSet());

            destinatario.setDirecciones(direcciones);
        }

        // Guardamos destinatario (y con cascade las direcciones si así está configurado)
        Destinatario saved = destinatarioRepository.save(destinatario);

        return new DestinatarioDTO(saved);
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


    public Destinatario destinatarioConDosDirecciones() {
        return destinatarioRepository.findDestinatarioByDireccionesEqualsTwo()
                .orElse(null);
    }

}





