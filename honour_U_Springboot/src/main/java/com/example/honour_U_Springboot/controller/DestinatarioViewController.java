package com.example.honour_U_Springboot.controller;

import com.example.honour_U_Springboot.model.Destinatario;
import com.example.honour_U_Springboot.model.Libro;
import com.example.honour_U_Springboot.service.DestinatarioService;
import com.example.honour_U_Springboot.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class DestinatarioViewController {
    @Autowired
    private DestinatarioService destinatarioService;

    @GetMapping("/destinatarios")
    public String mostrarDestinatarios(Model model) {
        List<Destinatario> destinatarios= destinatarioService.findAllDestinatarios();
        // Verificamos si los libros se recuperan correctamente (opcional para depuración)
        System.out.println("Destinatarios cargados: " + destinatarios);
        model.addAttribute("destinatarios", destinatarios);
        model.addAttribute("destinatario", new Destinatario()); // Para el formulario
        return "crearDestinatario"; // El nombre del archivo HTML (proyectos.html)
    }

    @PostMapping("/destinatarios")
    public String guardarDestinatario(Destinatario destinatario) {
        destinatarioService.saveDestinatario(destinatario);
        return "redirect:/destinatarios"; // Redirige para que recargue la lista
    }

    // Mostrar formulario de edición
    @GetMapping("/destinatarios/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Destinatario destinatario = destinatarioService.findDestinatarioById(id);
        model.addAttribute("destinatario", destinatario);
        return "editarDestinatario"; // Vista a crear
    }

    // Procesar el formulario de edición
    @PostMapping("/destinatarios/{id}/update")
    public String actualizarDestinatario(@PathVariable Long id, @ModelAttribute Destinatario destinatario) {
        destinatarioService.updateDestinatario(id, destinatario);
        return "redirect:/destinatarios";
    }

    //Eliminar un barco por ID
    @GetMapping("/destinatarios/{id}/delete")
    public String eliminarDestinatario(@PathVariable Long id) {
        destinatarioService.deleteDestinatarioById(id);
        return "redirect:/destinatarios";
    }
}

