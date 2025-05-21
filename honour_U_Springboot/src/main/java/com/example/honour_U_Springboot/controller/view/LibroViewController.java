package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.dto.ParticipanteDTO;
import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.model.Participante;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.LibroService;
import com.example.honour_U_Springboot.service.ParticipanteService;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LibroViewController {
    @Autowired
    private LibroService libroService;
    @Autowired
    private ProyectoService proyectoService;


    @PostMapping("/libros")
    public String guardarLibro(@ModelAttribute Libro libro) {
        Long proyectoId = libro.getProyecto() != null ? libro.getProyecto().getProyectoId() : null;
        if (proyectoId != null) {
            Proyecto proyecto = proyectoService.findById(proyectoId); // tu método para buscar proyecto
            libro.setProyecto(proyecto);
        }
        libroService.saveLibro(libro);
        return "redirect:/libros";
    }

    // Mostrar formulario de edición
    @GetMapping("/libros/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Libro libro = libroService.findLibroById(id);
        model.addAttribute("libro", libro);
        model.addAttribute("proyectos", proyectoService.findAll());
        return "editarLibro"; // Vista a crear
    }

    // Procesar el formulario de edición
    @PostMapping("/libros/{id}/update")
    public String actualizarLibro(@PathVariable Long id, @ModelAttribute Libro libro) {
        libroService.updateLibro(id, libro);
        return "redirect:/libros";
    }

    //Eliminar un proyecto por ID
    @GetMapping("/libros/{id}/delete")
    public String eliminarLibro(@PathVariable Long id) {
        libroService.deleteLibroByIdAPI(id);
        return "redirect:/libros";
    }

    @GetMapping("/libros")
    public String mostrarFormularioLibros(Model model) {
        model.addAttribute("libro", new Libro());
        model.addAttribute("libros", libroService.findAll());

        // Solo mostrar proyectos que no tienen libro asociado
        List<Proyecto> proyectosDisponibles = proyectoService.findAll().stream()
                .filter(p -> p.getLibro() == null)
                .collect(Collectors.toList());

        model.addAttribute("proyectos", proyectosDisponibles);

        return "crearLibro";
    }
    @GetMapping("/crearLibro")
    public String mostrarFormularioCrearLibro(Model model) {
        if (!model.containsAttribute("libro")) {
            model.addAttribute("libro", new Libro()); // Libro vacío para evitar null
        }
        return "crearLibro";
    }



}

