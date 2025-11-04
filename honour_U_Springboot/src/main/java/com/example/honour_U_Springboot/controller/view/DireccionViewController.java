package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Direccion;
import com.example.honour_U_Springboot.service.DestinatarioService;
import com.example.honour_U_Springboot.service.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Clase DireccionView Controller
 * Maneja las vistas del controlador
 */
@Controller
@RequestMapping("/backoffice")
public class DireccionViewController {
    @Autowired
    private DireccionService direccionService;
    private DestinatarioService destinatarioService;

    public DireccionViewController(DireccionService direccionService, DestinatarioService destinatarioService) {
        this.direccionService = direccionService;
        this.destinatarioService = destinatarioService;
    }

    /**
     * Método para mostrar direccioones
     * @param destinatarioId
     * @param model
     * @return la vista de crear dirección con la de direcciones
     * @throws Exception
     */
    @GetMapping("/direcciones")
    public String mostrarDirecciones(
            @RequestParam(required = false) Long destinatarioId,
            Model model) throws Exception {

        List<Direccion> direcciones= direccionService.findAllDirecciones();
        List<Destinatario> destinatarios = destinatarioService.findAllDestinatarios();

        model.addAttribute("direcciones", direcciones);

        // Creamos una nueva dirección para el formulario
        Direccion direccion = new Direccion();

        // Si llega destinatarioId, preseleccionamos ese destinatario
        if (destinatarioId != null) {
            Destinatario destinatario = destinatarioService.findDestinatarioById(destinatarioId);
            direccion.setDestinatario(destinatario);
        }

        model.addAttribute("direccion", direccion);
        model.addAttribute("destinatarios", destinatarios);

        return "backoffice/crearDireccion";
    }

    /**
     * Método guardar dirección
     * @param direccion que se quiere guardar
     * @param destinatarioId
     * @return la lista de direcciones actualizada
     * @throws Exception
     */
    @PostMapping("/direcciones")
    public String guardarDireccion(Direccion direccion, @RequestParam Long destinatarioId) throws Exception {
        Destinatario destinatario = destinatarioService.findDestinatarioById(destinatarioId);
        direccion.setDestinatario(destinatario);
        direccionService.saveDireccion(direccion);
        return "redirect:/backoffice/direcciones";
    }

    /**
     * Método para editar dirección
     * @param id a editar
     * @param model conecta el controlador con las vistas
     * @return vista de edición de dirección
     * @throws Exception si no la puede crear
     */
    @GetMapping("/direcciones/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Direccion direccion = direccionService.findDireccionById(id);
        List<Destinatario> destinatarios = destinatarioService.findAllDestinatarios();
        model.addAttribute("direccion", direccion);
        model.addAttribute("destinatarios", destinatarios);
        return "backoffice/editarDireccion"; // Vista a crear
    }

    /**
     * Método para actualizar dirección
     * @param id de la dirección a actualizar
     * @param direccion
     * @return vista de direcciones actualizada
     */
    @PostMapping("/direcciones/{id}/update")
    public String actualizarDireccion(@PathVariable Long id, @ModelAttribute Direccion direccion) {
        direccionService.updateDireccion(id,direccion);
        return "redirect:/backoffice/direcciones";
    }

    /**
     * Método eliminar dirección
     * @param id de la dirección a eliminar
     * @return la vista de las direcciones con la lista actualizada
     */
    @GetMapping("/direcciones/{id}/delete")
    public String eliminarDireccion(@PathVariable Long id) {
        direccionService.deleteDireccionById(id);
        return "redirect:/backoffice/direcciones";
    }

    /**
     * Método mostrarMapaLibros
     * @param model conecta el controlador con la vista
     * @return la vista del mapa
     */
    @GetMapping("/mapa-libros")
    public String mostrarMapaLibros(Model model) {
        Map<String, Long> librosPorPais = direccionService.contarLibrosPorPais();
        model.addAttribute("librosPorPais", librosPorPais);
        return "mapaLibros";
    }

}

