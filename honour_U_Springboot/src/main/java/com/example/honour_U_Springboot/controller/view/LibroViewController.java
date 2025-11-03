package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.LibroService;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase LibroViewController
 * Maneja las vistas del controlador
 */
@Controller
public class LibroViewController {
    @Autowired
    private LibroService libroService;
    @Autowired
    private ProyectoService proyectoService;


    /**
     * Método guardar libro
     * @param libro
     * @return vista actualizada con la lista de libros
     */
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

    /**
     * Método editar libros
     * @param id del libro actualizar
     * @param model conecta la inforamción del controlador con las vistas
     * @return vista para editar libro
     * @throws Exception si no la puede crear
     */
    @GetMapping("/libros/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Libro libro = libroService.findLibroById(id);
        model.addAttribute("libro", libro);
        model.addAttribute("proyectos", proyectoService.findAll());
        return "editarLibro"; // Vista a crear
    }

    /**
     * Método para actualizar libro
     * @param id del libro a actualizr
     * @param libro
     * @return la vista con los libros actualizados
     */
    @PostMapping("/libros/{id}/update")
    public String actualizarLibro(@PathVariable Long id, @ModelAttribute Libro libro) {
        libroService.updateLibro(id, libro);
        return "redirect:/libros";
    }

    /**
     * Método eliminar libro
     * @param id del libro para eliminar
     * @return la vista con la lista de libros actualizada
     */
    @GetMapping("/libros/{id}/delete")
    public String eliminarLibro(@PathVariable Long id) {
        libroService.deleteLibroById(id);
        return "redirect:/libros";
    }

    /**
     * Método para mostrar forumulario que muestra los libros
     * @param model conecta las vistas con el controlador
     * @return la vista con lista de libros actualizados
     */
    @GetMapping("/libros")
    public String mostrarFormularioLibros(Model model) {
        model.addAttribute("libro", new Libro());
        model.addAttribute("libros", libroService.findAll());

        // Solo mostrar proyectos que no tienen libro asociado
        List<Proyecto> proyectosDisponibles = proyectoService.findAll().stream()
                .filter(p -> p.getLibro() == null)
                .collect(Collectors.toList());

        model.addAttribute("proyectos", proyectosDisponibles);

        return "listaLibros";
    }

    /**
     * Método para crear libros
     * @param model conecta las vistas con el controlador
     * @return vista crear libro
     */
    @GetMapping("/crearLibro")
    public String mostrarFormularioCrearLibro(Model model) {
        if (!model.containsAttribute("libro")) {
            model.addAttribute("libro", new Libro()); // Libro vacío para evitar null
        }
        return "listaLibros";
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }




}

