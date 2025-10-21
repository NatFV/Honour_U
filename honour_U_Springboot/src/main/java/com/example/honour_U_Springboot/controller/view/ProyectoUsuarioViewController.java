package com.example.honour_U_Springboot.controller.view;

import com.example.honour_U_Springboot.model.Aportacion;
import com.example.honour_U_Springboot.model.Proyecto;
import com.example.honour_U_Springboot.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@Controller
@RequestMapping("/usuario/proyectos")
public class ProyectoUsuarioViewController {

    @Autowired
    private ProyectoService proyectoService;

    // Mostrar formulario para crear proyecto
    @GetMapping ("/nuevo")
    public String mostrarFormularioProyecto(Model model) {
        model.addAttribute("proyecto", new Proyecto());
        return "usuario/nuevoProyecto"; // apunta al template dentro de /usuario
    }

    // Guardar proyecto creado por el usuario
    // Guardar proyecto y mostrar enlaces (público + admin)
    @PostMapping("/nuevo")
    public String guardarProyecto(Proyecto proyecto, Model model) {

        // Generar tokens si faltan (público + admin)
        if (proyecto.getTokenUrl() == null || proyecto.getTokenUrl().isBlank()) {
            proyecto.setTokenUrl(UUID.randomUUID().toString());
        }
        if (proyecto.getAdminToken() == null || proyecto.getAdminToken().isBlank()) {
            proyecto.setAdminToken(UUID.randomUUID().toString());
        }

        // Construir base (http://host:port)
        String base = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .build()
                .toUriString();

        // Enlaces
        String linkAportaciones = base + "/proyectos/token/" + proyecto.getTokenUrl() + "/aportaciones";
        String linkAdmin        = base + "/proyectos/admin/" + proyecto.getAdminToken() + "/panel";

        // Asigna las URLS antes de guardar
        proyecto.setUrlProyecto(linkAportaciones);
        proyecto.setUrlAdmin(linkAdmin);

        // Persistir
        Proyecto guardado = proyectoService.saveProyecto(proyecto);


        // Pasar datos a la vista de enlaces
        model.addAttribute("proyecto", guardado);
        model.addAttribute("linkAportaciones", linkAportaciones);
        model.addAttribute("linkAdmin", linkAdmin);


        return "usuario/proyectoLinks"; // vista que muestra ambos enlaces
    }

    @GetMapping("/aportar/{token}")
    public String mostrarFormularioAportacion(@PathVariable String token, Model model) throws Exception {
        Proyecto proyecto = proyectoService.findByTokenUrl(token);
        if (proyecto == null) {
            throw new Exception("Proyecto no encontrado");
        }

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("nuevaAportacion", new Aportacion());
        return "OldAportacion"; // template para añadir aportaciones
    }
}

