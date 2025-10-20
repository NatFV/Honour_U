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

    /**
     * Método para guardar el libro en la base de datos.
     * @param libro
     * @return
     */
    public Libro saveLibro (Libro libro){
        //Llamamos al metodo save() de ProyectoRepository, que guarda el proyecto
        //en la base de datos
        return libroRepository.save(libro);
    }

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
     * Método obtener libros por país.
     * Devuelve una lista de libros acorde con el país
     * @param pais
     * @return
     */
    List<Libro> findLibrosByPais(String pais){
        return libroRepository.findLibrosByPais(pais);
    }


    // Crear un nuevo libro desde DTO
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

    // Obtener todos los libros en formato DTO
    public List<LibroDTO> getAllLibrosDTOAPI() {
        List<Libro> libros = libroRepository.findAll();
        return libros.stream().map(LibroDTO::new).collect(Collectors.toList());
    }

    // Obtener un libro por ID en formato DTO
    public LibroDTO findLibroByIdAPI(Long id) {
        Optional<Libro> libro = libroRepository.findById(id);
        return libro.map(LibroDTO::new).orElse(null);
    }

    // Actualizar un libro por ID
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

    // Eliminar un libro por ID
    public void deleteLibroByIdAPI(Long id) {
        libroRepository.deleteById(id);
    }

    public List<Libro> obtenerLibrosPorPais(String pais) {
        return libroRepository.findLibrosByPais(pais);
    }

    public List<Libro> findAll() {
        return libroRepository.findAll();
    }
}


