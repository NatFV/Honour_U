package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/backoffice/proyectos")
public class ProyectoBackofficeViewController {
    @Autowired
    private ProyectoService proyectoService;

    // Mostrar listado de proyectos
    @GetMapping
    public String mostrarListadoProyectos(Model model) {
        List<Proyecto> proyectos = proyectoService.findAllProyectos();
        model.addAttribute("proyectos", proyectos);
        return "backoffice/listadoProyectos";
    }

    // Mostrar formulario de edición para un proyecto
    @GetMapping("/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoById(id);
        model.addAttribute("proyecto", proyecto);
        return "editarProyecto";
    }

    // Guardar proyecto editado
    @PostMapping("/{id}/update")
    public String actualizarProyecto(@PathVariable Long id, @ModelAttribute Proyecto proyecto) {
        proyectoService.updateProyecto(id, proyecto);
        return "redirect:/backoffice/proyectos";
    }

    // Eliminar proyecto
    @GetMapping("/{id}/delete")
    public String eliminarProyecto(@PathVariable Long id) {
        proyectoService.deleteProyectoById(id);
        return "redirect:/backoffice/proyectos";
    }

}
