package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.dto.DireccionDTO;
import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Direccion;
import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.repository.DestinatarioRepository;
import com.example.honour_U_Springboot.repository.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DireccionService {
    @Autowired
    DireccionRepository direccionRepository;
    @Autowired
    private DestinatarioRepository destinatarioRepository;

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
    public Direccion findDireccionById(Long id) throws Exception {
        return direccionRepository.findById(id)
                .orElseThrow(() -> new Exception("Direccion not found with id " + id));
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
    public Direccion updateDireccion (Long id, Direccion direccion) {

        Direccion existente = direccionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada"));

        // Solo actualizamos los campos que pueden cambiar
        existente.setCalle(direccion.getCalle());
        existente.setPiso(direccion.getPiso());
        existente.setLetra(direccion.getLetra());
        existente.setCodigoPostal(direccion.getCodigoPostal());
        existente.setPais(direccion.getPais());

        return direccionRepository.save(existente);
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
    public List<Direccion> obtenerDireccionesInternacionales(){
        return direccionRepository.findByPaisNot("Spain");
    }

    public DireccionDTO createDireccionFromDTO(DireccionDTO dto) {
        Direccion direccion = new Direccion();
        direccion.setCalle(dto.getCalle());
        direccion.setPiso(dto.getPiso());
        direccion.setLetra(dto.getLetra());
        direccion.setCodigoPostal(dto.getCodigoPostal());
        direccion.setPais(dto.getPais());

        if (dto.getDestinatarioId() != null) {
            Destinatario destinatario = destinatarioRepository.findById(dto.getDestinatarioId())
                    .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
            direccion.setDestinatario(destinatario);
        }

        Direccion saved = direccionRepository.save(direccion);
        return new DireccionDTO(saved);
    }

    public List<DireccionDTO> getAllDireccionesDTO() {
        return direccionRepository.findAll()
                .stream()
                .map(DireccionDTO::new)
                .collect(Collectors.toList());
    }

    public DireccionDTO findDireccionByIdAPI(Long id) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        return new DireccionDTO(direccion);
    }

    public DireccionDTO updateDireccionAPI(Long id, DireccionDTO dto) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        direccion.setCalle(dto.getCalle());
        direccion.setPiso(dto.getPiso());
        direccion.setLetra(dto.getLetra());
        direccion.setCodigoPostal(dto.getCodigoPostal());
        direccion.setPais(dto.getPais());

        if (dto.getDestinatarioId() != null) {
            Destinatario destinatario = destinatarioRepository.findById(dto.getDestinatarioId())
                    .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
            direccion.setDestinatario(destinatario);
        } else {
            direccion.setDestinatario(null);
        }

        Direccion updated = direccionRepository.save(direccion);
        return new DireccionDTO(updated);
    }

    public void deleteDireccionByIdAPI(Long id) {
        if (!direccionRepository.existsById(id)) {
            throw new RuntimeException("Dirección no encontrada");
        }
        direccionRepository.deleteById(id);
    }

    /**
     * Método contarLibrosPorPais
     * Este método se utiliza para el template que muestra el mapa
     * @return un HashMap que devuelve el pais y la cantidad de libros que hay por cada país
     */
    public Map<String, Long> contarLibrosPorPais() {
        List<Object[]> resultados = direccionRepository.countLibrosPorPais();
        Map<String, Long> mapaLibros = new HashMap<>();
        for (Object[] fila : resultados) {
            String pais = (String) fila[0];
            Long cantidad = (Long) fila[1];
            mapaLibros.put(pais, cantidad);
        }
        return mapaLibros;
    }

}






