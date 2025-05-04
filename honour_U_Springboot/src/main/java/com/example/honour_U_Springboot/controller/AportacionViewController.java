package com.example.honour_U_Springboot.controller;

import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.AportacionService;
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
        return "redirect:/aportaciones"; // Redirige para que recargue la lista
    }

    // Mostrar formulario de edición
    @GetMapping("/aportaciones/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) throws Exception {
        Aportacion aportacion = aportacionService.findAportacionById(id);
        model.addAttribute("aportacion", aportacion);
        return "editarAportacion"; // Vista a crear
    }

    // Procesar el formulario de edición
    @PostMapping("/aportaciones/{id}/update")
    public String actualizarAportacion(@PathVariable Long id, @ModelAttribute Aportacion aportacion) {
        aportacionService.updateAportacion(id, aportacion);
        return "redirect:/aportaciones";
    }

    //Eliminar un barco por ID
    @GetMapping("/aportaciones/{id}/delete")
    public String eliminarAportaciones(@PathVariable Long id) {
        aportacionService.deleteAportacionById(id);
        return "redirect:/aportaciones";
    }
}
