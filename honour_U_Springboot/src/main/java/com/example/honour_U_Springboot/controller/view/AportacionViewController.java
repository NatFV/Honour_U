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

/**
 * Clase AportacionViewController
 * Maneja las vistas del controlador
 */
@Controller
public class AportacionViewController {
  @Autowired
    private AportacionService aportacionService;

   @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ParticipanteService participanteService;

    /**
     * Método mostrar aport
     * @param model para pasar información del controlador a la vista
     * @return la lista en el html crearAportación
     */
   @GetMapping("/aportaciones")
    public String mostrarAportaciones(Model model) {
        List<Aportacion> aportaciones = aportacionService.findAllAportaciones();
        // Verificamos si los proyectos se recuperan correctamente (opcional para depuración)
        System.out.println("Aportaciones cargadas: " + aportaciones);
        model.addAttribute("aportaciones", aportaciones);
        model.addAttribute("aportacion", new Aportacion()); // Para el formulario
        return "crearAportacion"; // El nombre del archivo HTML .html)
    }

    /**
     * Método guardar aportaciones
     * @param aportacion con la información de la aportación
     * @return la lista actualizada de aportaciones en proyectos
     */
   // @PostMapping("/aportaciones")
    public String guardarAportacion(Aportacion aportacion) {
        aportacionService.saveAportacion(aportacion);
        Long proyectoId = aportacion.getProyecto().getProyectoId(); // Obtener el id del proyecto asociado
        return "redirect:/proyectos/" + proyectoId + "/edit";
    }

    /**
     * Método para editar aportaciones
     * @param id de la aportación que se quiere editar
     * @param model conecta vista con controlador
     * @return la vista editar aportacion
     * @throws Exception si no la encuentra
     */
    @GetMapping("/aportaciones/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Aportacion aportacion = aportacionService.findAportacionById(id);
        List<Participante> participantes = participanteService.findAll();  // Trae todos los participantes

        model.addAttribute("aportacion", aportacion);
        model.addAttribute("participantes", participantes);  // Pasa la lista al modelo

        return "editarAportacion"; // Vista a crear
    }

    /**
     * Método para actualizar aportación
     * @param id
     * @param aportacionForm
     * @return redirige a la página de gestión de aportaciones
     * @throws Exception si no se puede actualizar
     */
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

    /**
     * Método para eliminar aportación
     * @param id de la aportación a eliminar
     * @return redirige a la lista de aportaciones
     */
   @GetMapping("/aportaciones/{id}/delete")
    public String eliminarAportaciones(@PathVariable Long id) {
        aportacionService.deleteAportacionById(id);
        return "redirect:/aportaciones";
    }

    /**
     * Método para gestionar aportaciones
     * @param id
     * @param model
     * @return la vista de gestión de aportaciones
     * @throws Exception si no se puede obtener
     */
   @GetMapping("/proyectos/{id}/aportaciones")
    public String gestionarAportaciones(@PathVariable Long id, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findProyectoByIdAPI(id).toEntity();
        List<Aportacion> aportaciones = aportacionService.findByProyecto(proyecto);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("aportaciones", aportaciones);
        model.addAttribute("nuevaAportacion", new Aportacion());

        return "gestionarAportaciones"; // nueva vista
    }

    /**
     * Método para gestionar participantes
     * @param id
     * @param model
     * @return vista de los partipantes a gestionar
     * @throws Exception si no se puede gestionar
     */
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
