package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Direccion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.DestinatarioService;
import com.example.honour_U_Springboot.service.DireccionService;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class DireccionViewController {
    @Autowired
    private DireccionService direccionService;
    private DestinatarioService destinatarioService;

    public DireccionViewController(DireccionService direccionService, DestinatarioService destinatarioService) {
        this.direccionService = direccionService;
        this.destinatarioService = destinatarioService;
    }
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

        return "crearDireccion";
    }

    @PostMapping("/direcciones")
    public String guardarDireccion(Direccion direccion, @RequestParam Long destinatarioId) throws Exception {
        Destinatario destinatario = destinatarioService.findDestinatarioById(destinatarioId);
        direccion.setDestinatario(destinatario);
        direccionService.saveDireccion(direccion);
        return "redirect:/direcciones";
    }

    // Mostrar formulario de edición
    @GetMapping("/direcciones/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Direccion direccion = direccionService.findDireccionById(id);
        List<Destinatario> destinatarios = destinatarioService.findAllDestinatarios();
        model.addAttribute("direccion", direccion);
        model.addAttribute("destinatarios", destinatarios);
        return "editarDireccion"; // Vista a crear
    }

    // Procesar el formulario de edición
    @PostMapping("/direcciones/{id}/update")
    public String actualizarDireccion(@PathVariable Long id, @ModelAttribute Direccion direccion) {
        direccionService.updateDireccion(id,direccion);
        return "redirect:/direcciones";
    }

    //Eliminar un proyecto por ID
    @GetMapping("/direcciones/{id}/delete")
    public String eliminarDireccion(@PathVariable Long id) {
        direccionService.deleteDireccionById(id);
        return "redirect:/direcciones";
    }

    @GetMapping("/mapa-libros")
    public String mostrarMapaLibros(Model model) {
        Map<String, Long> librosPorPais = direccionService.contarLibrosPorPais();
        model.addAttribute("librosPorPais", librosPorPais);
        return "mapaLibros"; // Nombre del archivo Thymeleaf
    }

}

