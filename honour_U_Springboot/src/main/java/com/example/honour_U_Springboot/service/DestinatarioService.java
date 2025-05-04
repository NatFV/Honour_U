package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.repository.DestinatarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<Destinatario> findDestinatarioById(Long id){
        return destinatarioRepository.findById(id);
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
    public Destinatario updateDestinatario (Long id, Destinatario destinatario){

        return destinatarioRepository.save(destinatario);
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

}

