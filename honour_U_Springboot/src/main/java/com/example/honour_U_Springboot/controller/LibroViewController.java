package com.example.honour_U_Springboot.controller;

import com.example.honour_U_Springboot.dto.ParticipanteDTO;
import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.model.Participante;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.LibroService;
import com.example.honour_U_Springboot.service.ParticipanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class LibroViewController {
    @Autowired
    private LibroService libroService;

    @GetMapping("/libros")
    public String mostrarLibros(Model model) {
        List<Libro> libros= libroService.findAllLibros();
        // Verificamos si los libros se recuperan correctamente (opcional para depuración)
        System.out.println("Libros cargados: " + libros);
        model.addAttribute("libros", libros);
        model.addAttribute("libro", new Libro()); // Para el formulario
        return "crearLibro"; // El nombre del archivo HTML (proyectos.html)
    }

    @PostMapping("/libros")
    public String guardarLibro(Libro libro) {
        libroService.saveLibro(libro);
        return "redirect:/libros"; // Redirige para que recargue la lista
    }

    // Mostrar formulario de edición
    @GetMapping("/libros/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Libro libro = libroService.findLibroById(id);
        model.addAttribute("libro", libro);
        return "editarLibro"; // Vista a crear
    }

    // Procesar el formulario de edición
    @PostMapping("/libros/{id}/update")
    public String actualizarLibro(@PathVariable Long id, @ModelAttribute Libro libro) {
        libroService.updateLibro(id, libro);
        return "redirect:/libros";
    }

    //Eliminar un barco por ID
    @GetMapping("/libros/{id}/delete")
    public String eliminarLibro(@PathVariable Long id) {
        libroService.deleteLibroById(id);
        return "redirect:/libros";
    }
}

