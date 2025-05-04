package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.model.Direccion;
import com.example.honour_U_Springboot.repository.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionService {
    @Autowired
    DireccionRepository direccionRepository;

    /**
     * Método para guardar la dirección en la base de datos.
     * @param direccion
     * @return
     */
    public Direccion saveDireccion (Direccion direccion){
        //Llamamos al metodo save() de DestinatarioRepository, que guarda el destinatario
        //en la base de datos
        return direccionRepository.save(direccion);
    }

    /**
     * Método para obtener la dirección por su ID
     * @param id
     * @return dirección
     */
    public Optional<Direccion> findDireccionById(Long id){
        return direccionRepository.findById(id);
    }

    /**
     * Método para obtener todas las direcciones
     * @return una lista con todos las direcciones
     */
    public List<Direccion> findAllDirecciones(){
        return direccionRepository.findAll();
    }

    /**
     * Método para actualizar una direccioón en la base de datos
     * @param direccion
     * @return dirección actualizada
     */
    public Direccion updateDireccion (Long id, Direccion direccion){

        return direccionRepository.save(direccion);
    }

    /**
     * Método para eliminar la dirección por su ID
     * @param id
     */
    public void deleteDireccionById (Long id){
        direccionRepository.deleteById(id);
    }


    /**
     * Método para obtener las direcciones internacionales
     *
     * @return lista de direcciones que no son de España
     */
    List<Direccion> obtenerDireccionesInternacionales(){
        return direccionRepository.findByPaisNot("Spain");
    }
}

