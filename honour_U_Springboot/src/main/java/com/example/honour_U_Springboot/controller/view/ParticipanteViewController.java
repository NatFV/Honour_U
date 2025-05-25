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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Clase ParticipanteViewController, maneja las vistas del controlador
 */
@Controller
public class ParticipanteViewController {

    @Autowired
    private ParticipanteService participanteService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private AportacionService aportacionService;

    /**
     * Método para listar los participantes de un proyecto
     * @param proyectoId
     * @param model pasa los datos a la vista
     * @return los participantes
     * @throws Exception
     */
    @GetMapping("/participantes/proyectos/{proyectoId}")
    public String listarParticipantesPorProyecto(@PathVariable Long proyectoId, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(proyectoId).toEntity();
        List<Participante> participantes = participanteService.findByProyectoId(proyectoId);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("participantes", participantes);
        model.addAttribute("participante", new Participante()); // para formulario

        return "crearParticipante"; // plantilla para listar y agregar participantes
    }

    /**
     * Método para guardar un participante  en el proyecto
     * @param proyectoId
     * @param participante
     * @return participante guardado
     * @throws Exception
     */
    @PostMapping("/participantes/proyectos/{proyectoId}")
    public String guardarParticipanteEnProyecto(@PathVariable Long proyectoId,
                                                @ModelAttribute Participante participante) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(proyectoId).toEntity();
        participante.setProyecto(proyecto);
        participanteService.saveParticipante(participante);

        return "redirect:/participantes/proyectos/" + proyectoId;
    }

    /**
     * Método para editar participante
     * @param id del participante
     * @param model pasa los datos a la vista
     * @return el formulario para editar participante
     * @throws Exception
     */
    @GetMapping("/participantes/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Participante participante = participanteService.findParticipanteById(id);
        model.addAttribute("participante", participante);
        model.addAttribute("aportaciones", participante.getAportaciones());
        return "editarParticipante";
    }

    /**
     * Método para actualizar participante
     * @param id
     * @param participante
     * @return Participante actualizado
     * @throws Exception
     */
    @PostMapping("/participantes/{id}/update")
    public String actualizarParticipante(@PathVariable Long id, @ModelAttribute Participante participante) throws Exception {
        Participante participanteOriginal = participanteService.findParticipanteById(id);

        // Actualizar solo los campos modificables
        participanteOriginal.setNombre(participante.getNombre());
        participanteOriginal.setApellido(participante.getApellido());
        participanteOriginal.setEmail(participante.getEmail());
        // No tocar proyecto ni otras relaciones

        participanteService.updateParticipante(id, participanteOriginal);

        Long proyectoId = participanteOriginal.getProyecto() != null ? participanteOriginal.getProyecto().getProyectoId() : null;
        if (proyectoId != null) {
            return "redirect:/participantes/proyectos/" + proyectoId;
        }
        return "redirect:/participantes";
    }

    /**
     * Método para eliminar participante
     * @param id
     * @return lista de participantes actualizada
     * @throws Exception si no se puede eliminar
     */
    @GetMapping("/participantes/{id}/delete")
    public String eliminarParticipante(@PathVariable Long id) throws Exception {
        Participante participante = participanteService.findParticipanteById(id);
        Long proyectoId = participante.getProyecto() != null ? participante.getProyecto().getProyectoId() : null;
        participanteService.deleteParticipanteById(id);
        if (proyectoId != null) {
            return "redirect:/participantes/proyectos/" + proyectoId;
        }
        return "redirect:/participantes";
    }

    /**
     * Método para listar los participantes del proyecto
     * @param model pasa los datos a la vista
     * @return vista que muestra todos los participantes
     * @throws Exception
     */
    @GetMapping("/participantes")
    public String listarTodosParticipantes(Model model) throws Exception {
        List<Participante> participantes = participanteService.findAllParticipantes();
        model.addAttribute("participantes", participantes);
        return "crearParticipante";
    }

}
