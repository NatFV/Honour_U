package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Participante;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.AportacionService;
import com.example.honour_U_Springboot.service.ParticipanteService;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Clase AportacionBackofficeViewController
 * Maneja las vistas del controlador para el backoffice del listado de aportaciones
 * Permite editar,crear, eliminar y actualizar aportaciones desde el backoffice
 */
@Controller
@RequestMapping("/backoffice/proyectos")
public class AportacionBackofficeViewController {
  @Autowired
    private AportacionService aportacionService;

   @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ParticipanteService participanteService;

    /**
     * Método para editar las aportaciones
     * @param id del proyecto del que queremos editar las aportaciones
     * @param editId parámetro opcional que indica la aportación a editar
     * @param model para conectar los datos con la vista
     * @return la vista con el listado de aportaciones
     * @throws Exception
     */
    @GetMapping("/{id}/aportaciones")
    public String gestionarAportacionesBackoffice(
            @PathVariable Long id,
            @RequestParam(value = "editId", required = false) Long editId,
            Model model) throws Exception {
        //A través de proyectoService se identifica el proyecto y se cargan las aportaciones
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(id).toEntity();
        List<Aportacion> aportaciones = aportacionService.findByProyecto(proyecto);

        //Aportación para editar o crear
        Aportacion aptEditarCrear;
        if (editId != null) {
            aptEditarCrear = aportacionService.findAportacionById(editId); // cargar para editar
        } else {
            aptEditarCrear = new Aportacion(); // objeto vacío para crear
        }

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportaciones", aportaciones);
        model.addAttribute("aportacion", aptEditarCrear);
        model.addAttribute("editMode", editId != null);

        return "backoffice/listadoAportaciones";
    }

    /**
     * Método para que editar una aportación en la misma página
     * @param projectId
     * @param aportacionId
     * @return la página de la aportación que se edita
     */
    // EDIT → redirige a la misma página con editId
    @GetMapping("/{projectId}/aportaciones/{aportacionId}/edit")
    public String editarEnMismaPagina(@PathVariable Long projectId, @PathVariable Long aportacionId) {
        return "redirect:/backoffice/proyectos/" + projectId + "/aportaciones?editId=" + aportacionId;
    }

    /**
     * Método para crear aportación dentro de un proyecto en el back office
     * @param id
     * @param aportacion
     * @return la página actualizada de proyectos con la nueva aportación
     * @throws Exception si no la encuentra
     */
    @PostMapping("/{id}/aportaciones")
    public String crearAportacion(@PathVariable Long id, @ModelAttribute Aportacion aportacion) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(id).toEntity();
        aportacion.setProyecto(proyecto);
        aportacionService.saveAportacion(aportacion);
        return "redirect:/backoffice/proyectos/" + id + "/aportaciones";
    }

    /**
     * Método para actualizar aportación desde el backoffice
     * @param id del proyecto
     * @param aportacionId el id de la aportación
     * @param form objeto comando que Spring rellena con datos que vienen en la petición de POST
     * @return la página de aportaciones de proyecto actualizada
     * @throws Exception
     */
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

    /**
     * Método para eliminar las aportaciones dentro del backoffice
     * @param projectId
     * @param aportacionId
     * @return el template actualizado con las aportaciones de proyecto.
     */
    @GetMapping("/{projectId}/aportaciones/{aportacionId}/delete")
    public String eliminarAportacionBackoffice(@PathVariable Long projectId,
                                               @PathVariable Long aportacionId) {
        aportacionService.deleteAportacionById(aportacionId);
        return "redirect:/backoffice/proyectos/" + projectId + "/aportaciones";
    }

}
