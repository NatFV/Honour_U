package com.example.honour_U_Springboot.service;

import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {
    @Autowired
    private LibroRepository libroRepository;
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
    public Optional<Libro> findLibroById(Long id){
        return libroRepository.findById(id);
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
        return libroRepository.save(libro);
    }

    /**
     * Método para eliminar un libro por su ID
     * @param id
     */
    public void deleteLibroById (Long id){
        libroRepository.deleteById(id);
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

}
