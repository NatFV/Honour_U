package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.AportacionService;
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
    @Autowired private AportacionService aportacionService;

    /**
     * Método que muestra la lista completa de proyectos en el backoffice
     * @param model que añade un atributo proyectos con una instancia de proyectos para que pueda enlazarse
     *              con los campos del formulario
     * @return el nombre de la vista
     */
    @GetMapping
    public String mostrarListadoProyectos(Model model) {
        List<Proyecto> proyectos = proyectoService.findAllProyectos();
        model.addAttribute("proyectos", proyectos);
        return "backoffice/listadoProyectos";
    }

    /**
     * Método para editar proyecto
     * @param id del proyecto que se utilizará para obtener el proyecto y sus aportaciones
     * @param model se añaden los atributos de proyecto y aportaciones con las instancias de Proyecto y
     *              aportaciones de ese proyecto para que se puedan enlazar con los campos de la vista
     * @return el nombre de la vista (backoffice/edicionProyecto)
     * @throws Exception en caso de no encontrar el proyecto.
     */
    @GetMapping("/{id}/edit")
    public String editar(@PathVariable Long id, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoById(id);
        List<Aportacion> aportaciones = aportacionService.findByProyecto(proyecto);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportaciones", aportaciones);
        return "backoffice/edicionProyecto";
    }

    /**
     * Método para guardar el proyecto editado y actualizarlo
     * @param id con el proyecto que se quiere actualizar
     * @param proyecto : datos del proyecto que se queire actualizar
     * @return la vista que muestra la lista de proyectos actualizada
     */
    @PostMapping("/{id}/update")
    public String actualizarProyecto(@PathVariable Long id, @ModelAttribute Proyecto proyecto) {
        proyectoService.updateProyecto(id, proyecto);
        return "redirect:/backoffice/proyectos";
    }

    /**
     * Método para eliminar proyecto
     * @param id del proyecto que se quiere actualizar
     * @return la vista de proyectos actualizada
     */
    @GetMapping("/{id}/delete")
    public String eliminarProyecto(@PathVariable Long id) {
        proyectoService.deleteProyectoById(id);
        return "redirect:/backoffice/proyectos";
    }


}


