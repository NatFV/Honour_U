package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.dto.LibroDTO;
import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.repository.LibroRepository;
import com.example.honour_U_Springboot.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Clase LibroService, contiene la lógica de la clase Libro
 * @author Natalia
 * @version 1
 */
@Service
public class LibroService {
    @Autowired
    private LibroRepository libroRepository;
    private final ProyectoRepository proyectoRepository;

    @Autowired
    public LibroService(LibroRepository libroRepository, ProyectoRepository proyectoRepository) {
        this.libroRepository = libroRepository;
        this.proyectoRepository = proyectoRepository;
    }

    /**CREACIÓN DE LIBROS**/
    /**
     * Método para guardar el libro en la base de datos.
     * @param libro que se desea guardar
     * @return libro guardado
     */
    public Libro saveLibro (Libro libro){
        //Llamamos al metodo save() de ProyectoRepository, que guarda el proyecto
        //en la base de datos
        return libroRepository.save(libro);
    }

    /**
     * Método para crear un libro DTO
     * @param libroDTO que se desea guardar
     *                 Se hace un set con los atributos que se desean guardar
     * @return libro guardado
     */
    public LibroDTO createLibroFromDTOAPI(LibroDTO libroDTO) {
        Libro libro = new Libro();
        libro.setTituloLibro(libroDTO.getTituloLibro());
        libro.setFormato(libroDTO.getFormato());
        libro.setCopias(libroDTO.getCopias());
        libro.setPaginas(libroDTO.getPaginas());

        // Si el DTO tiene un proyecto, lo seteamos
        if (libroDTO.getProyecto() != null) {
            libro.setProyecto(libroDTO.getProyecto().toEntity());
        }

        libro = libroRepository.save(libro);
        return new LibroDTO(libro);
    }

    /** ACTUALIZACIÓN DE LIBROS **/
    /**
     * Método para actualizar un libro en la base de datos
     * @param libro
     * @return libro actualizado
     */
    public Libro updateLibro (Long id, Libro libro){
        Libro existente = libroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        // Solo actualizamos los campos que pueden cambiar
        existente.setTituloLibro(libro.getTituloLibro());
        existente.setFormato(libro.getFormato());
        existente.setCopias(libro.getCopias());
        existente.setPaginas(libro.getPaginas());

        return libroRepository.save(existente); // Esto actualiza, no crea nuevo
    }

    /**
     * Método para actualizar un libro DTO
     * @param id del libro
     * @param updatedLibroDTO que se quiere actualizar
     * @return libro actualizado si existe libro y null si no existe
     */
    public LibroDTO updateLibroAPI(Long id, LibroDTO updatedLibroDTO) {
        Optional<Libro> libroOptional = libroRepository.findById(id);
        if (libroOptional.isPresent()) {
            Libro libro = libroOptional.get();
            libro.setTituloLibro(updatedLibroDTO.getTituloLibro());
            libro.setFormato(updatedLibroDTO.getFormato());
            libro.setCopias(updatedLibroDTO.getCopias());
            libro.setPaginas(updatedLibroDTO.getPaginas());

            // Actualizamos el proyecto si es necesario
            if (updatedLibroDTO.getProyecto() != null) {
                libro.setProyecto(updatedLibroDTO.getProyecto().toEntity());
            }

            libro = libroRepository.save(libro);
            return new LibroDTO(libro);
        }
        return null; // Si no existe el libro
    }

    /** ELIMINACIÓN DE LIBROS**/

    /**
     * Método para eliminar un libro por su ID
     * @param id
     */
    @Transactional
    public void deleteLibroById(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + id));

        // Romper relación con Proyecto (lado inverso)
        Proyecto proyecto = libro.getProyecto();
        if (proyecto != null) {
            proyecto.setLibro(null);   // provoca eliminación huérfana si usas orphanRemoval en Proyecto
            libro.setProyecto(null);
            proyectoRepository.save(proyecto);
        }

        // Si no usas orphanRemoval en Destinatario, libera explícitamente:
        if (libro.getDestinatario() != null) {
            libro.setDestinatario(null);
        }

        libroRepository.delete(libro);
        // Fuerza sincronización inmediata (evita ver el libro tras el redirect)
        libroRepository.flush();
    }

    /**
     * Método para eliminar un Libro DTO
     * @param id del libro
     */
    public void deleteLibroByIdAPI(Long id) {
        libroRepository.deleteById(id);
    }

    /** BÚSQUEDAS **/

    /**
     * Método para obtener el libro por su ID
     * @param id
     * @return libro
     */
    public Libro findLibroById(Long id)throws Exception {

        return libroRepository.findById(id)
                .orElseThrow(() -> new Exception("Proyecto not found with id " + id));
    }

    /**
     * Método para obtener todos los libros
     * @return una lista con todos los libros
     */
    public List<Libro> findAllLibros(){
        return libroRepository.findAll();
    }


    /**
     * Método obtener libros por país.
     * Devuelve una lista de libros acorde con el país
     * @param pais
     * @return
     */
    public List<Libro> obtenerLibrosPorPais(String pais) {
        return libroRepository.findLibrosByPais(pais);
    }


    /**
     * Método para obtener todos los libros DTO
     * @return lista de libros DTO
     */
    public List<LibroDTO> getAllLibrosDTOAPI() {
        List<Libro> libros = libroRepository.findAll();
        return libros.stream().map(LibroDTO::new).collect(Collectors.toList());
    }

    /**
     * Método para obtener libros DTO por ID
     * @param id del libro DTO
     * @return libro encontrado o null si no lo encuentra
     */
    public LibroDTO findLibroByIdAPI(Long id) {
        Optional<Libro> libro = libroRepository.findById(id);
        return libro.map(LibroDTO::new).orElse(null);
    }






    public List<Libro> findAll() {
        return libroRepository.findAll();
    }
}


