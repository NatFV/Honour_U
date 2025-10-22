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

    // Mostrar listado de proyectos
    @GetMapping
    public String mostrarListadoProyectos(Model model) {
        List<Proyecto> proyectos = proyectoService.findAllProyectos();
        model.addAttribute("proyectos", proyectos);
        return "backoffice/listadoProyectos";
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable Long id, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(id).toEntity(); // o findProyectoById(id)
        List<Aportacion> aportaciones = aportacionService.findByProyecto(proyecto);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportaciones", aportaciones);
        return "backoffice/edicionProyecto";
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

    // LISTAR + FORM (crear/editar en la misma vista)
    @GetMapping("/{id}/aportaciones")
    public String gestionarAportacionesBackoffice(
            @PathVariable Long id,
            @RequestParam(value = "editId", required = false) Long editId,
            Model model) throws Exception {

        Proyecto proyecto = proyectoService.findProyectoByIdAPI(id).toEntity();
        List<Aportacion> aportaciones = aportacionService.findByProyecto(proyecto);

        Aportacion formBean = (editId != null)
                ? aportacionService.findAportacionById(editId)   // carga la que vas a editar
                : new Aportacion();                               // creación

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportaciones", aportaciones);
        model.addAttribute("aportacion", formBean);
        model.addAttribute("editMode", editId != null);

        return "backoffice/listadoAportaciones";
    }

    // EDIT → redirige a la misma página con editId
    @GetMapping("/{projectId}/aportaciones/{aportacionId}/edit")
    public String editarEnMismaPagina(@PathVariable Long projectId, @PathVariable Long aportacionId) {
        return "redirect:/backoffice/proyectos/" + projectId + "/aportaciones?editId=" + aportacionId;
    }

    // CREAR
    @PostMapping("/{id}/aportaciones")
    public String crearAportacion(@PathVariable Long id, @ModelAttribute Aportacion aportacion) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(id).toEntity();
        aportacion.setProyecto(proyecto);
        aportacionService.saveAportacion(aportacion);
        return "redirect:/backoffice/proyectos/" + id + "/aportaciones";
    }

    // ACTUALIZAR
    @PostMapping("/{id}/aportaciones/{aportacionId}")
    public String actualizarAportacion(@PathVariable Long id,
                                       @PathVariable Long aportacionId,
                                       @ModelAttribute Aportacion form) throws Exception {
        Aportacion existente = aportacionService.findAportacionById(aportacionId);
        existente.setMensaje(form.getMensaje());
        existente.setRemitente(form.getRemitente());
        existente.setUrl(form.getUrl());
        existente.setMediaType(form.getMediaType());
        existente.setEsVisible(form.isEsVisible());
        aportacionService.saveAportacion(existente);
        return "redirect:/backoffice/proyectos/" + id + "/aportaciones";
    }

    @GetMapping("/{projectId}/aportaciones/{aportacionId}/delete")
    public String eliminarAportacionBackoffice(@PathVariable Long projectId,
                                               @PathVariable Long aportacionId) {
        aportacionService.deleteAportacionById(aportacionId);
        return "redirect:/backoffice/proyectos/" + projectId + "/aportaciones";
    }

}


