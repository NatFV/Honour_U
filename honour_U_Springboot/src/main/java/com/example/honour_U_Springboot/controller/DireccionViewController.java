package com.example.honour_U_Springboot.controller;

import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Direccion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.DestinatarioService;
import com.example.honour_U_Springboot.service.DireccionService;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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
    public String mostrarDirecciones(Model model) {
        List<Direccion> direcciones= direccionService.findAllDirecciones();
        List<Destinatario> destinatarios = destinatarioService.findAllDestinatarios();
        // Verificamos si los proyectos se recuperan correctamente (opcional para depuración)
        System.out.println("Direcciones cargadas: " + direcciones);
        model.addAttribute("direcciones", direcciones);
        model.addAttribute("direccion", new Direccion());
        model.addAttribute("destinatarios", destinatarios);// Para el formulario
        return "crearDireccion"; // El nombre del archivo HTML (proyectos.html)
    }

    @PostMapping("/direcciones")
    public String guardarDireccion(Direccion direccion) {
        direccionService.saveDireccion(direccion);
        return "redirect:/direcciones"; // Redirige para que recargue la lista
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

    //Eliminar un barco por ID
    @GetMapping("/direcciones/{id}/delete")
    public String eliminarDireccion(@PathVariable Long id) {
        direccionService.deleteDireccionById(id);
        return "redirect:/direcciones";
    }
}

