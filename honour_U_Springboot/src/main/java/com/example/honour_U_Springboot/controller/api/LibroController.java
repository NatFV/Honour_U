package com.example.honour_U_Springboot.controller.api;

import com.example.honour_U_Springboot.dto.LibroDTO;
import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.LibroService;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //Hace que los métodos devuelvan JSON directamente
@RequestMapping("/api/libros") // Ruta base para los puntos finales de los Libros
public class LibroController {

    @Autowired
    private LibroService libroService; // Servicio que manejará la lógica de negocio para Libros
    @Autowired
    private ProyectoService proyectoService;

    /**
     * Método para crear un nuevo libro
     * @param libroDTO con los datos necesarios para crearlo
     * @return un objeto de tipo LibroDTO
     */
    @PostMapping
    public ResponseEntity<LibroDTO> createLibro(@RequestBody LibroDTO libroDTO) {
        try {
            LibroDTO creado = libroService.createLibroFromDTOAPI(libroDTO);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para obtener todos los libros
     * @return una lista con todos los libros
     */
    @GetMapping
    public ResponseEntity<List<LibroDTO>> findAllLibros() {
        try {
            List<LibroDTO> librosDTO = libroService.getAllLibrosDTOAPI();

            if (librosDTO.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(librosDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para encontrar libros por id
     * @param id, nos permite identificar un libro
     * @return la información de ese libro o error indicando que no lo encuentra
     */
    @GetMapping("/{id}")
    public ResponseEntity<LibroDTO> findLibroById(@PathVariable Long id) {
        try {
            LibroDTO libroDTO = libroService.findLibroByIdAPI(id);
            if (libroDTO == null) {
                return ResponseEntity.notFound().build(); // Si no se encuentra, retornar 404
            }
            return ResponseEntity.ok(libroDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para actualizar un libro
     * @param id para encontrar el libro
     * @param updatedLibroDTO para actualizar la información
     * @return un código de estado HTTP 200 OK que indica que la actualización fue exitosa
     */
    @PutMapping("/{id}")
    public ResponseEntity<LibroDTO> updateLibro(@PathVariable Long id, @RequestBody LibroDTO updatedLibroDTO) {
        try {
            LibroDTO libroDTO = libroService.updateLibroAPI(id, updatedLibroDTO);
            return ResponseEntity.ok(libroDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método para eliminar libro
     * @param id para identificar el libro a eliminar
     * @return un código de estado que indica si se pudo eliminar
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibro(@PathVariable Long id) {
        try {
            libroService.deleteLibroByIdAPI(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Método que muestra la información de libros según un país indicado
     * @param pais para indicar un país deseado
     * @return una lista de libros que han sido enviados a ese país
     */
    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<Libro>> obtenerLibrosPorPais(@PathVariable String pais) {
        List<Libro> libros = libroService.obtenerLibrosPorPais(pais);
        if (libros.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(libros);
    }

    @PostMapping("/libros")
    public String guardarLibro(@ModelAttribute Libro libro, @RequestParam("proyectoId") Long proyectoId) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoById(proyectoId);
        libro.setProyecto(proyecto); // Asignamos el proyecto al libro

        libroService.saveLibro(libro); // Persistimos el libro con la FK al proyecto
        return "redirect:/proyectos";
    }

}
