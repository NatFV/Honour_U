package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.dto.ParticipanteDTO;
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

@Controller
public class ParticipanteViewController {
    @Autowired
    private ParticipanteService participanteService;
    @Autowired
    private ProyectoService proyectoService;
    @Autowired
    private AportacionService aportacionService;

    @GetMapping("/participantes")
    public String mostrarParticipantes(Model model) {

            List<ParticipanteDTO> participantes = participanteService.findAllParticipanteDTOs();
            model.addAttribute("participantes", participantes);
            model.addAttribute("participante", new Participante()); // para el formulario
            return "crearParticipante";

    }

    @PostMapping("/participantes")
    public String guardarParticipante(@ModelAttribute Participante participante, @RequestParam("proyecto.proyectoId") Long proyectoId) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(proyectoId).toEntity();
        participante.setProyecto(proyecto);  // asignamos proyecto para que no sea null

        participanteService.saveParticipante(participante);
        return "redirect:/participantes/proyectos/" + proyectoId;  // redirigir a la lista actualizada del proyecto
    }

    // Mostrar formulario de edición
    @GetMapping("/participantes/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Participante participante = participanteService.findParticipanteById(id);
        model.addAttribute("participante", participante);
        model.addAttribute("aportaciones", participante.getAportaciones()); // Asegúrate de pasar las aportaciones
        return "editarParticipante"; // Vista a crear
    }

    // Procesar el formulario de edición
    @PostMapping("/participantes/{id}/update")
    public String actualizarParticipante(@PathVariable Long id, @ModelAttribute Participante participante) {
        participanteService.updateParticipante(id, participante);
        return "redirect:/participantes";
    }

    //Eliminar un participantes por ID
    @GetMapping("/participantes/{id}/delete")
    public String eliminarParticipantes(@PathVariable Long id) {
        participanteService.deleteParticipanteById(id);
        return "redirect:/participantes";
    }

    @GetMapping("/participantes/proyectos/{id}")
    public String gestionarParticipantes(@PathVariable Long id, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(id).toEntity();

        List<Aportacion> aportaciones = aportacionService.findByProyecto(proyecto);

        // Extraemos participantes únicos de las aportaciones
        Set<Participante> participantes = aportaciones.stream()
                .map(Aportacion::getParticipante)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("participantes", participantes);
        // Nuevo participante para el formulario
        model.addAttribute("nuevoParticipante", new Participante());

        return "gestionarParticipantes";  // nueva plantilla Thymeleaf que crearás
    }

    @PostMapping("/participantes/proyectos/{id}")
    public String agregarParticipanteProyecto(@PathVariable Long id, @ModelAttribute Participante participante, Model model) throws Exception {
        // Buscas el proyecto
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(id).toEntity();

        // Aquí asignar la relación, si es necesaria
        // Si en Participante tienes referencia a Proyecto, setearla
        // Si no, asegúrate que la lógica esté correcta

        // Guarda el participante
        participanteService.saveParticipante(participante);

        // Ahora recarga la lista de participantes para ese proyecto
        // (opcional: si saveParticipante no actualiza la relación, asegurarse de ello)

        // Redirige a la misma página para ver la lista actualizada
        return "redirect:/participantes/proyectos/" + id;
    }







}

