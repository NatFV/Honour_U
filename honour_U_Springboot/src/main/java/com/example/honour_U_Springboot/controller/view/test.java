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
@RequestMapping("/proyectos")
public class test {
    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private AportacionService aportacionService;

    /**
     * Método para listar las aportaciones de un proyecto
     * @param token
     * @param model
     * @return
     * @throws Exception
     */
    @GetMapping("/{token}/aportaciones")
    public String listarAportacionesPorProyecto(@PathVariable String token, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(token); // usa tu método para buscar por token
        List<Aportacion> aportaciones = aportacionService.findByProyecto(proyecto);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportaciones", aportaciones);
        return "backoffice/listadoAportaciones";
    }


    /**
     * Método para editar una aportación concreta
     * @param tokenUrl
     * @param aportacionId
     * @param model
     * @return
     * @throws Exception
     */
    @GetMapping("/{tokenUrl}/aportaciones/{aportacionId}/edit")
    public String editarAportacion(@PathVariable String tokenUrl,
                                   @PathVariable Long aportacionId,
                                   Model model) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(tokenUrl);
        Aportacion aportacion = aportacionService.findAportacionById(aportacionId);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportacion", aportacion);
        return "backoffice/editarAportacion";
    }

    /**
     * Método para guardar la edición
     * @param tokenUrl
     * @param aportacionId
     * @param aportacionForm
     * @return
     * @throws Exception
     */
    @PostMapping("/{tokenUrl}/aportaciones/{aportacionId}/update")
    public String actualizarAportacion(@PathVariable String tokenUrl,
                                       @PathVariable Long aportacionId,
                                       @ModelAttribute Aportacion aportacionForm) throws Exception {
        Aportacion aportacion = aportacionService.findAportacionById(aportacionId);

        aportacion.setMensaje(aportacionForm.getMensaje());
        aportacion.setRemitente(aportacionForm.getRemitente());
        aportacion.setUrl(aportacionForm.getUrl());
        aportacion.setMediaType(aportacionForm.getMediaType());
        aportacion.setEsVisible(aportacionForm.isEsVisible());

        aportacionService.saveAportacion(aportacion);

        return "redirect:/proyectos/" + tokenUrl + "/aportaciones";
    }




}
