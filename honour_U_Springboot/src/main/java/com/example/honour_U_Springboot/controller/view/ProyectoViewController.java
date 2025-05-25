package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.service.AportacionService;


import java.util.List;

/**
 * Clase ProyectoViewController, maneja las vistas del controlador
 */
@Controller
public class ProyectoViewController {
    @Autowired
    private ProyectoService proyectoService;
    @Autowired
    private AportacionService aportacionService;

    /**
     * Método para mostrar proyectos
     * @param model
     * @return la vista del proyecto
     */
    @GetMapping("/proyectos")
    public String mostrarProyectos(Model model) {
        List<Proyecto> proyectos = proyectoService.findAllProyectos();
        // Verificamos si los proyectos se recuperan correctamente (opcional para depuración)
        System.out.println("Proyectos cargados: " + proyectos);
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("proyecto", new Proyecto()); // Para el formulario
        return "crearProyecto"; // El nombre del archivo HTML (proyectos.html)
    }

    /**
     * Método para guardar el proyecto
     * @param proyecto
     * @return redirige a la lista de proyectos actualizados
     */
    @PostMapping("/proyectos")
    public String guardarProyecto(Proyecto proyecto) {
        proyectoService.saveProyecto(proyecto);
        return "redirect:/proyectos"; // Redirige para que recargue la lista
    }

    /**
     * Muestra el formulario de edición para un proyecto específico.
     * Carga el proyecto, sus aportaciones y prepara el modelo para la vista de edición.
     *
     * @param id    ID del proyecto a editar.
     * @param model Modelo para pasar datos a la vista.
     * @return      Nombre de la vista de edición del proyecto.
     * @throws Exception si el proyecto no se encuentra o ocurre un error en el servicio.
     */
    @GetMapping("/proyectos/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoById(id);
        model.addAttribute("proyecto", proyecto);

        List<Aportacion> aportaciones = aportacionService.findByProyectoId(id); // Este método debe existir
        model.addAttribute("aportaciones", aportaciones);

        model.addAttribute("nuevaAportacion", new Aportacion()); // Para el formulario

        return "editarProyecto";
    }

    /**
     * Método para actualizar proyecto
     * @param id
     * @param proyecto
     * @return la vista proyecto actualizada
     */
    @PostMapping("/proyectos/{id}/update")
    public String actualizarProyecto(@PathVariable Long id, @ModelAttribute Proyecto proyecto) {
        proyectoService.updateProyecto(id, proyecto);
        return "redirect:/proyectos";
    }

    /**
     * Método eliminar proyecto
     * @param id del proyecto deseado
     * @return la vista de proyectos con la lista actualizad
     */
    @GetMapping("/proyectos/{id}/delete")
    public String eliminarProyecto(@PathVariable Long id) {
        proyectoService.deleteProyectoById(id);
        return "redirect:/proyectos";
    }
}

