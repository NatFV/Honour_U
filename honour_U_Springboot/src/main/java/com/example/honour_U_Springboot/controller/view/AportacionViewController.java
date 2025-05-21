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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class AportacionViewController {
    @Autowired
    private AportacionService aportacionService;

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ParticipanteService participanteService;

    @GetMapping("/aportaciones")
    public String mostrarAportaciones(Model model) {
        List<Aportacion> aportaciones = aportacionService.findAllAportaciones();
        // Verificamos si los proyectos se recuperan correctamente (opcional para depuración)
        System.out.println("Aportaciones cargadas: " + aportaciones);
        model.addAttribute("aportaciones", aportaciones);
        model.addAttribute("aportacion", new Aportacion()); // Para el formulario
        return "crearAportacion"; // El nombre del archivo HTML (proyectos.html)
    }

    @PostMapping("/aportaciones")
    public String guardarAportacion(Aportacion aportacion) {
        aportacionService.saveAportacion(aportacion);
        Long proyectoId = aportacion.getProyecto().getProyectoId(); // Obtener el id del proyecto asociado
        return "redirect:/proyectos/" + proyectoId + "/edit";
    }

    // Mostrar formulario de edición
    @GetMapping("/aportaciones/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Aportacion aportacion = aportacionService.findAportacionById(id);
        List<Participante> participantes = participanteService.findAll();  // Trae todos los participantes

        model.addAttribute("aportacion", aportacion);
        model.addAttribute("participantes", participantes);  // Pasa la lista al modelo

        return "editarAportacion"; // Vista a crear
    }

    // Procesar el formulario de edición
    @PostMapping("/aportaciones/{id}/update")
    public String actualizarAportacion(@PathVariable Long id, @ModelAttribute Aportacion aportacionForm) throws Exception {
        Aportacion existente = aportacionService.findAportacionById(id);

        // Actualiza los campos necesarios
        existente.setMensaje(aportacionForm.getMensaje());
        existente.setRemitente(aportacionForm.getRemitente());
        existente.setUrl(aportacionForm.getUrl());
        existente.setMediaType(aportacionForm.getMediaType());
        existente.setEsVisible(aportacionForm.isEsVisible());

        // Guarda los cambios
        aportacionService.saveAportacion(existente);

        // Obtiene el ID del proyecto al que pertenece esta aportación
        Long proyectoId = existente.getProyecto().getProyectoId();

        // Redirige a la página de gestión de aportaciones
        return "redirect:/proyectos/" + proyectoId + "/aportaciones";
    }

    //Eliminar un barco por ID
    @GetMapping("/aportaciones/{id}/delete")
    public String eliminarAportaciones(@PathVariable Long id) {
        aportacionService.deleteAportacionById(id);
        return "redirect:/aportaciones";
    }

    @GetMapping("/proyectos/{id}/aportaciones")
    public String gestionarAportaciones(@PathVariable Long id, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(id).toEntity();
        List<Aportacion> aportaciones = aportacionService.findByProyecto(proyecto);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportaciones", aportaciones);
        model.addAttribute("nuevaAportacion", new Aportacion());

        return "gestionarAportaciones"; // nueva vista
    }

    @GetMapping("/proyectos/{id}/participantes")
    public String gestionarParticipantes(@PathVariable Long id, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(id).toEntity();
        List<Participante> participantes = participanteService.findByProyecto(proyecto); // O findAll() si no tienes filtro

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("participantes", participantes);
        model.addAttribute("nuevoParticipante", new Participante());

        return "gestionarParticipantes"; // nombre del template Thymeleaf a crear
    }

}
