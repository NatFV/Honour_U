package com.example.honour_U_Springboot.controller;

import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProyectoViewController {
    @Autowired
    private ProyectoService proyectoService;

    @GetMapping("/proyectos")
    public String mostrarProyectos(Model model) {
        List<Proyecto> proyectos = proyectoService.findAllProyectos();
        // Verificamos si los proyectos se recuperan correctamente (opcional para depuración)
        System.out.println("Proyectos cargados: " + proyectos);
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("proyecto", new Proyecto()); // Para el formulario
        return "crearProyecto"; // El nombre del archivo HTML (proyectos.html)
    }

    @PostMapping("/proyectos")
    public String guardarProyecto(Proyecto proyecto) {
        proyectoService.saveProyecto(proyecto);
        return "redirect:/proyectos"; // Redirige para que recargue la lista
    }

    // Mostrar formulario de edición
    @GetMapping("/proyectos/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoById(id);
        model.addAttribute("proyecto", proyecto);
        return "editarProyecto"; // Vista a crear
    }

    // Procesar el formulario de edición
    @PostMapping("/proyectos/{id}/update")
    public String actualizarProyecto(@PathVariable Long id, @ModelAttribute Proyecto proyecto) {
        proyectoService.updateProyecto(id, proyecto);
        return "redirect:/proyectos";
    }

    //Eliminar un barco por ID
    @GetMapping("/proyectos/{id}/delete")
    public String eliminarProyecto(@PathVariable Long id) {
        proyectoService.deleteProyectoById(id);
        return "redirect:/proyectos";
    }
}

